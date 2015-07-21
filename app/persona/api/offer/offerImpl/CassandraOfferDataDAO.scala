package persona.api.offer.offerImpl

import java.util.UUID

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl.{context => _, _}
import com.websudos.phantom.keys.PartitionKey
import org.joda.time.DateTime
import persona.api.offer.Offer

import scala.concurrent.{Future}


class OfferDataTable extends CassandraTable[OfferDataTable, Offer] {

  object creationDay extends StringColumn(this) with PartitionKey[String]
  object timeID extends TimeUUIDColumn(this) with ClusteringOrder[UUID] with Descending
  object description extends StringColumn(this)
  object expirationTime extends DateTimeColumn(this)
  object currentParticipants extends IntColumn(this)
  object maxParticipants extends IntColumn(this)
  object value extends DoubleColumn(this)
  object criteria extends MapColumn[OfferDataTable, Offer, String, String](this)

  def fromRow(row: Row): Offer = {
    new Offer(                      // TODO: possibly remove the new
      timeID(row),
      DateTime.parse(creationDay(row)), // TODO: format?
      description(row),
      expirationTime(row),
      currentParticipants(row),
      maxParticipants(row),
      value(row),
      criteria(row)
    )
  }

}


class CassandraOfferDataDAO extends OfferDataTable with OfferDAO with SimpleCassandraConnector {

  def list: Future[Seq[Offer]] = {
    select.fetch.map(_.toSeq)
  }

    // TODO:limit actually 25?
  def get(id: UUID): Future[Seq[Offer]] = {
    select.where(_.timeID eqs id)
      .limit(25)
      .fetch
      .map(_.toSeq)
  }

  // TODO: Taylor actually implement this
  implicit def keySpace: KeySpace = KeySpace("persona")
}
