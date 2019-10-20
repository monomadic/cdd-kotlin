import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Request(var name: String,
              var vars: MutableList<Variable>,
              var method: String,
              var urlPath: String)


