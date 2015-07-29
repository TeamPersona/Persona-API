package persona.api.account.personal

import java.util.UUID

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl.{context => _, _}
import com.websudos.phantom.keys.PartitionKey
import persona.api.authentication.User
import persona.db.PersonaCassandraConnector

import scala.concurrent.{ExecutionContext, Future}

class PersonalDataTable extends CassandraTable[PersonalDataTable, DataItem] {

  object user_id extends UUIDColumn(this) with PartitionKey[UUID]
  object creation_time extends DateTimeColumn(this) with ClusteringOrder[DateTime] with Descending
  object category extends StringColumn(this)
  object subcategory extends StringColumn(this)
  object data extends MapColumn[PersonalDataTable, DataItem, String, String](this)

  override def tableName = "personal"

  def fromRow(row: Row): DataItem = {
    DataItem(
      user_id(row),
      creation_time(row),
      category(row),
      subcategory(row),
      data(row)
    )
  }

}

class CassandraPersonalDataDAO extends PersonalDataTable with PersonalDataDAO with PersonaCassandraConnector {

  def listInformation(user: User)(implicit ec: ExecutionContext): Future[Seq[DataItem]] = {
    select.where(_.user_id eqs user.id)
          .fetch
          .map(_.toSeq)
  }

  def saveInformation(dataItem: DataItem)(implicit ec: ExecutionContext): Future[ResultSet] = {
    insert.value(_.user_id, dataItem.userID)
          .value(_.creation_time, dataItem.creationTime)
          .value(_.category, dataItem.category)
          .value(_.subcategory, dataItem.subcategory)
          .value(_.data, dataItem.data)
          .future()
  }

}
