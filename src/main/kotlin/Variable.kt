import com.github.ajalt.clikt.output.TermUi.echo
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
}
@Serializable
data class ArrayType(var Array: Type)
@Serializable
data class ComplexType(var Complex: String)


@Serializer(forClass = Type::class)  // 1
object CardTypeSerializer {

    override val descriptor: SerialDescriptor
        get() = StringDescriptor   // 2

    @ImplicitReflectionSerializer
    override fun deserialize(decoder: Decoder): Type {  // 3
        var result: Type = Type.BOOL
        result = try {
            decode(decoder.decodeString())
        } catch (e: kotlinx.serialization.json.JsonDecodingException) {
            try {
                val res = decoder.decode<ArrayType>()
                Type.ARRAY(res.Array)
            }
            catch (e: kotlinx.serialization.json.JsonDecodingException) {
                val res = decoder.decode<ComplexType>()
                Type.COMPLEX(res.Complex)
            }
        }

        return result
    }

    private fun decode(raw: String): Type {
        return  when (raw) {
            "String" -> Type.STRING
            "Int" -> Type.INT
            "Float" -> Type.FLOAT
            "Bool" -> Type.BOOL
            else -> {
                echo(raw)
                if (raw[0].toString() == "[")  {
                    decode(raw.removePrefix("[").removeSuffix("]"))
                }
                else {
                    Type.COMPLEX(raw)
                }
            }
        }
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, obj: Type) {  // 4
        return when (obj) {
            is Type.INT -> encoder.encodeString("Int")
            is Type.STRING -> encoder.encodeString("String")
            is Type.FLOAT -> encoder.encodeString("Float")
            is Type.BOOL -> encoder.encodeString("Bool")
            is Type.ARRAY -> encoder.encode(ArrayType(obj.type))
            is Type.COMPLEX -> encoder.encode(ComplexType(obj.type))
        }
    }
}