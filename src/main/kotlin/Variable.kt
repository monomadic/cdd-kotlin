import org.jetbrains.kotlin.utils.rethrow
import java.util.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.*

//@Serializable
//data class JSONVariable {
//
//}

@Serializable
data class Variable(var name: String,
               var type: Type,
               var value: String?,
               var optional: Boolean) {

}


@Serializable(with = CardTypeSerializer::class)
sealed class Type {
    object INT : Type()
    object STRING : Type()
    object FLOAT : Type()
    object BOOL : Type()
    class ARRAY(val type:Type) : Type()
    class COMPLEX(val type:String) : Type()

    fun jsonValue(): String {
        return Type.jsonValue(this)
    }

    companion object {
        fun jsonValue(type:Type): String {
            return when (type) {
                is Type.INT -> "Int"
                is Type.STRING -> "String"
                is Type.FLOAT -> "Float"
                is Type.BOOL -> "Bool"
                is Type.ARRAY -> "[" + Type.jsonValue(type.type) + "]"
                is Type.COMPLEX -> type.type
            }
        }
    }
}

@Serializer(forClass = Type::class)  // 1
object CardTypeSerializer {

    override val descriptor: SerialDescriptor
        get() = StringDescriptor   // 2

    override fun deserialize(decoder: Decoder): Type {  // 3
        return  decode(decoder.decodeString())
    }

    private fun decode(raw: String): Type {
        return  when (raw) {
            "String" -> Type.STRING
            "Int" -> Type.INT
            "Float" -> Type.FLOAT
            "Bool" -> Type.BOOL
            else -> {
                if (raw[0].toString() == "[")  {
                    decode(raw.removePrefix("[").removeSuffix("]"))
                }
                else {
                    Type.COMPLEX(raw)
                }
            }
        }
    }

    override fun serialize(encoder: Encoder, obj: Type) {  // 4
        encoder.encodeString(obj.jsonValue())
    }
}