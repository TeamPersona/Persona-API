package com.persona.service.bank

import spray.json._

trait DataSchemaJsonProtocol extends DefaultJsonProtocol with FieldDescriptorJsonProtocol {

  implicit object DataSchemaJsonFormat extends RootJsonFormat[DataSchema] {

    private[this] def readCategory(dataSchema: JsObject) = {
      dataSchema.getFields("category") match {
        case Seq(JsString(category)) => category
        case _ => throw new DeserializationException("Invalid data schema category")
      }
    }

    private[this] def writeCategory(dataSchema: DataSchema) = {
      "category" -> JsString(dataSchema.category)
    }

    private[this] def readSubcategory(dataSchema: JsObject) = {
      dataSchema.getFields("subcategory") match {
        case Seq(JsString(subcategory)) => subcategory
        case _ => throw new DeserializationException("Invalid data schema subcategory")
      }
    }

    private[this] def writeSubcategory(dataSchema: DataSchema) = {
      "subcategory" -> JsString(dataSchema.subcategory)
    }

    private[this] def readRewardPoints(dataSchema: JsObject) = {
      dataSchema.getFields("rewardPoints") match {
        case Seq(JsNumber(rewardPoints)) => rewardPoints.intValue
        case _ => throw new DeserializationException("Invalid data schema reward points")
      }
    }

    private[this] def writeRewardPoints(dataSchema: DataSchema) = {
      "rewardPoints" -> JsNumber(dataSchema.rewardPoints)
    }

    private[this] def readFields(dataSchema: JsObject) = {
      dataSchema.getFields("fields") match {
        case Seq(fields: JsArray) => fields.convertTo[Seq[FieldDescriptor]]
        case _ => throw new DeserializationException("Invalid data schema fields")
      }
    }

    private[this] def writeFields(dataSchema: DataSchema) = {
      "fields" -> dataSchema.fieldDescriptors.values.toJson
    }

    def write(dataSchema: DataSchema): JsValue = {
      JsObject(
        writeCategory(dataSchema),
        writeSubcategory(dataSchema),
        writeRewardPoints(dataSchema),
        writeFields(dataSchema)
      )
    }

    def read(dataSchema: JsValue): DataSchema = {
      val dataSchemaAsJsObject = dataSchema.asJsObject
      val category = readCategory(dataSchemaAsJsObject)
      val subcategory = readSubcategory(dataSchemaAsJsObject)
      val rewardPoints = readRewardPoints(dataSchemaAsJsObject)
      val fields = readFields(dataSchemaAsJsObject)

      DataSchema(category, subcategory, rewardPoints, fields)
    }

  }

}
