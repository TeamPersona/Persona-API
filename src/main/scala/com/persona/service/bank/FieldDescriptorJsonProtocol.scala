package com.persona.service.bank

import spray.json._

trait FieldDescriptorJsonProtocol extends DefaultJsonProtocol {

  implicit object FieldDescriptorJsonFormat extends RootJsonFormat[FieldDescriptor] {

    private[this] def readName(fieldDescriptor: JsObject) = {
      fieldDescriptor.getFields("name") match {
        case Seq(JsString(name)) => name
        case _ => throw new DeserializationException("Invalid field descriptor name")
      }
    }

    private[this] def writeName(fieldDescriptor: FieldDescriptor) = {
      "name" -> JsString(fieldDescriptor.name)
    }

    private[this] def readIsRequired(fieldDescriptor: JsObject) = {
      fieldDescriptor.getFields("required") match {
        case Seq(JsBoolean(isRequired)) => isRequired
        case _ => throw new DeserializationException("Invalid field descriptor required")
      }
    }

    private[this] def writeIsRequired(fieldDescriptor: FieldDescriptor) = {
      "required" -> JsBoolean(fieldDescriptor.isRequired)
    }

    private[this] def readFieldType(fieldDescriptor: JsObject) = {
      fieldDescriptor.getFields("type") match {
        case Seq(JsString(fieldType)) => fieldType
        case _ => throw new DeserializationException("Invalid field descriptor type")
      }
    }

    private[this] def writeFieldType(fieldDescriptor: FieldDescriptor) = {
      "type" -> JsString(fieldDescriptor.fieldType)
    }

    def write(fieldDescriptor: FieldDescriptor): JsValue = {
      JsObject(
        writeName(fieldDescriptor),
        writeIsRequired(fieldDescriptor),
        writeFieldType(fieldDescriptor)
      )
    }

    def read(fieldDescriptor: JsValue): FieldDescriptor = {
      val fieldDescriptorAsJsObject = fieldDescriptor.asJsObject
      val name = readName(fieldDescriptorAsJsObject)
      val isRequired = readIsRequired(fieldDescriptorAsJsObject)
      val fieldType = readFieldType(fieldDescriptorAsJsObject)

      FieldDescriptor(name, isRequired, fieldType)
    }

  }

}
