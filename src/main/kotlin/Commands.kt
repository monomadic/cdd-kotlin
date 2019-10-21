import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kastree.ast.psi.Parser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import parser.SourceParser
import sourceWriter.SourceWriter
import java.io.File

class Cdd : NoRunCliktCommand() {
    // You could load the aliases from a config file etc.

    override fun aliases(): Map<String, List<String>> = mapOf(
        "ci" to listOf("commit"),
        "cm" to listOf("commit", "-m")
    )
}

class ListModels: CliktCommand(name = "list-models") {
    private val path: String by option(help = "Path to Source file").required()
    override fun run() {

        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val project = SourceParser(file).generateProject()

        val json = Json(JsonConfiguration.Stable)
        echo(json.stringify(Model.serializer().list, project.models))
    }
}

class ListRequests: CliktCommand(name = "list-requests") {
    private val path: String by option(help = "Path to Source file").required()
    override fun run() {

        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val project = SourceParser(file).generateProject()

        val json = Json(JsonConfiguration.Stable)

        echo(json.stringify(Request.serializer().list, project.requests))
    }
}

class InsertModel: CliktCommand(name = "insert-model") {
    private val path: String by option(help = "Path to Source file").required()
    private val json: String by option(help = "JSON").required()
    override fun run() {
//        val json = File("jsonSample.txt").readText()
//        println(json)
        val parser = Json(JsonConfiguration.Stable)
        val model = parser.parse(Model.serializer(),json)
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.insert(model)
        writer.write(path)
    }
}

class InsertRequest: CliktCommand(name = "insert-request") {
    private val path: String by option(help = "Path to Source file").required()
        private val json: String by option(help = "JSON").required()
    override fun run() {
//        val json = File("jsonSample.txt").readText()
//        println(json)
        val parser = Json(JsonConfiguration.Stable)
        val request = parser.parse(Request.serializer(),json)
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.insert(request)
        writer.write(path)
    }
}

class DeleteClass: CliktCommand(name = "delete-class") {
    private val path: String by option(help = "Path to Source file").required()
    private val name: String by option(help = "Class Name").required()
    override fun run() {
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.deleteClass(name)
        writer.write(path)
    }
}

class DeleteModel: CliktCommand(name = "delete-model") {
    private val path: String by option(help = "Path to Source file").required()
    private val name: String by option(help = "Class Name").required()
    override fun run() {
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.deleteClass(name)
        writer.write(path)
    }
}

class DeleteRequest: CliktCommand(name = "delete-request") {
    private val path: String by option(help = "Path to Source file").required()
    private val name: String by option(help = "Class Name").required()
    override fun run() {
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.deleteClass(name)
        writer.write(path)
    }
}

class UpdateRequest: CliktCommand(name = "update-request") {
    private val path: String by option(help = "Path to Source file").required()
    private val json: String by option(help = "JSON").required()
    override fun run() {
//        val json = File("jsonSample.txt").readText()
//        println(json)
        val parser = Json(JsonConfiguration.Stable)
        val request = parser.parse(Request.serializer(),json)
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.update(request)
        writer.write(path)
    }
}

class UpdateModel: CliktCommand(name = "update-model") {
    private val path: String by option(help = "Path to Source file").required()
    private val json: String by option(help = "JSON").required()
    override fun run() {
//        val json = File("jsonSample.txt").readText()
//        println(json)
        val parser = Json(JsonConfiguration.Stable)
        val model = parser.parse(Model.serializer(),json)
        val text = File(path).readText()
        val file = Parser.parseFile(text)
        val writer = SourceWriter(file)
        writer.update(model)
        writer.write(path)
    }
}