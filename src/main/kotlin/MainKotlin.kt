import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required
import kastree.ast.psi.Parser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import parser.SourceParser
import sourceWriter.SourceWriter
import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.kotlin.com.google.common.collect.ImmutableList


class  MainKotlin {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val list = args.toMutableList()
            if (args.first() == DeleteClass().commandName || args.first() == DeleteModel().commandName || args.first() == DeleteRequest().commandName) {
                list.add(1,"--path")
                list.add(3,"--name")
            } else
            if (args.first() == ListModels().commandName || args.first() == ListRequests().commandName || args.first() == DeleteClass().commandName) {
                list.add(1,"--path")
            }
            else {
                list.add(1,"--path")
                list.add(3,"--json")
            }
            Cdd().subcommands(ListModels(),ListRequests(),InsertModel(),InsertRequest(),DeleteClass(), DeleteModel(), DeleteRequest() ,UpdateRequest(),UpdateModel()).main(list.toTypedArray())
        }
//        {
//            val text = File("localizationToIOS.sh").readText()
//            val file = Parser.parseFile(text)
//            val project = SourceParser(file).generateProject()
//            val json = Json(JsonConfiguration.Stable)
//            println(project.requests.toString())
//
//            val jsonData = json.stringify(Request.serializer(), project.requests.first())
//            println(jsonData)
//
//
//            val it = json.parse(Request.serializer(), jsonData)
//            val newData = json.stringify(Request.serializer(), it)
//            println(newData)
//            File("jsonSample.txt").writeText(newData)
//        }
//            println("found request: " + it.name)
//            it.vars.forEach {variable ->
//                if (variable.value != null) {
//                    println(" value: " + variable.value)
//                }
//            }

//        }
//            println(Commit().commandHelp)
//            println("--")
//            val fileName = "localizationToIOS.sh"
//            val text = File(fileName).readText()
//            val file = Parser.parseFile(text)
//
//            val project = SourceParser(file).generateProject()
//            val writer = SourceWriter(file)
//            writer.insert(project.models.first())
//            writer.write()
//            writer.delete(project.models.first())
//            writer.write()
//            val request = Request("TestRequest", emptyList<Variable>().toMutableList(),"post","dad{name}dada")
//            writer.insert(request)
//            writer.write()
//        }
    }
}


