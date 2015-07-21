package persona.api.offer.offerImpl

import java.util.UUID

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl.{context => _, _}
import com.websudos.phantom.keys.PartitionKey
import org.joda.time.DateTime
import persona.api.offer.Offer

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global


class OfferDataTable extends CassandraTable[OfferDataTable, Offer] {

  object creationDay extends StringColumn(this) with PartitionKey[String]
  object timeID extends StringColumn(this) with ClusteringOrder[String] with Descending // TODO: will have to change to UUID when can guet UUID in Routes
  object description extends StringColumn(this)
  object expirationTime extends DateTimeColumn(this)
  object currentParticipants extends IntColumn(this)
  object maxParticipants extends IntColumn(this)
  object value extends DoubleColumn(this)
  object criteria extends MapColumn[OfferDataTable, Offer, String, String](this)

  def fromRow(row: Row): Offer = {
    new Offer(                      // TODO: possibly remove the new
      timeID(row).toString,
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

  // TODO:limit actually 25 for these two?
  def list: Future[Seq[Offer]] = {
    select
      .limit(25)
      .fetch
      .map(_.toSeq)
  }

  def get(id: String): Future[Seq[Offer]] = {
    select.where(_.timeID eqs id)
      .fetch
      .map(_.toSeq)
  }

  // TODO:  actually implement this
  implicit def keySpace: KeySpace = KeySpace("persona")
}
