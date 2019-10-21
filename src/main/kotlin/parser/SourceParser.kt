package parser
import kastree.ast.Node
import kastree.ast.Visitor
import kastree.ast.psi.Parser
import kastree.ast.Writer
import java.io.File
import Model
import Project
import Request
import javafx.scene.Parent

class SourceParser(var file: Node.File) {

    fun generateProject(): Project {
        val models = mutableListOf<Model>()
        val requests = mutableListOf<Request>()
        Visitor.visit(file) { v, _ ->
            if (v is Node.Decl.Structured) {
                var isRequest = false
                val parent = v.parents.firstOrNull()
                if (parent is Node.Decl.Structured.Parent.Type) {
                    val type = ExtractorType().extract(parent.type)
                    if (type is Type.COMPLEX) {
                        if (type.type == "APIRequest") {
                            isRequest = true
                        }
                    }
                }
                val vars = ExtractorVariables().extract(v)
                if (isRequest) {
                    val urlPath = vars.find { it.name == "url_path" }?.value
                    val method = vars.find { it.name == "method" }?.value
                    val response_type = vars.find { it.name == "response_type" }?.value
                    val error_type = vars.find { it.name == "error_type" }?.value
                    val reservedVars =  listOf("path","method","response_type","error_type")
                    if (method != null && urlPath != null && response_type != null && error_type != null) {
                        vars.removeAll {
                            reservedVars.contains(it.name)
                        }
                        requests.add(Request(v.name,vars, method, urlPath,response_type,error_type))
                    }

                }
                else
                models.add(Model(v.name,vars))
            }

        }


//        models.forEach {
//            println("found class: " + it.name)
//            it.vars.forEach {variable ->
//                if (variable.value != null) {
//                    println(" value: " + variable.value)
//                }
//            }
//        }
//
//        requests.forEach {
//            println("found request: " + it.name)
//            it.vars.forEach {variable ->
//                if (variable.value != null) {
//                    println(" value: " + variable.value)
//                }
//            }
//        }

        return Project(models,requests)
    }
}