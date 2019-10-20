package sourceWriter
import kastree.ast.Node
import kastree.ast.psi.Parser
import kastree.ast.Writer
import java.io.File
import Model
import Type
import Variable
import Request
import com.sun.xml.internal.bind.v2.model.core.TypeRef
import kastree.ast.MutableVisitor
import kastree.ast.Visitor
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult
import parser.SourceParser

class SourceWriter(var file: Node.File) {
    fun insert(request: Request) {
        val urlPath = createUrlPath(request.urlPath)
        val t = createType(Type.COMPLEX("APIRequest")) //as? TypeRef.Simple
        val type = Node.Decl.Structured.Parent.Type(t as Node.TypeRef.Simple,null)
        val method = createMethod(request.method)
        insertClass(request.name,request.vars, listOf(urlPath,method), listOf(type))
    }

    fun update(request: Request) {
        updateVarsInClass(request.name,request.vars)
        val urlPath = createUrlPath(request.urlPath)
        val method = createMethod(request.method)

        val properties = listOf(urlPath, method).mapNotNull {
            it.vars.firstOrNull()
        }
        updatePropertiesInClass(request.name,properties)
    }

    fun insert(model: Model) {
        insertClass(model.name,model.vars, emptyList(), emptyList())
    }

    fun update(model: Model) {
        updateVarsInClass(model.name,model.vars)
    }

    fun write(path:String){
        File(path).writeText(Writer.write(file))
    }
    
    private fun insertClass(name: String, vars: MutableList<Variable>, properties:List<Node.Decl.Property>, parents: List<Node.Decl.Structured.Parent.Type>) {
        val constructorVars = vars.map {
            createConstructorProperty(it)
        }

        val constructor = Node.Decl.Structured.PrimaryConstructor(emptyList(),constructorVars)
        val annotation = Node.Modifier.AnnotationSet.Annotation(listOf("Serializable"), emptyList(),emptyList())
        val lit = Node.Modifier.Lit(Node.Modifier.Keyword.DATA)
        val set = Node.Modifier.AnnotationSet(null, listOf(annotation))
        val classCode = Node.Decl.Structured(
            listOf(set,lit),
            Node.Decl.Structured.Form.CLASS,
            name,
            emptyList(),
            constructor,
            emptyList(),
            parents,
            emptyList(),
            properties)

//        classCode.typeParams.first().
        val models = file.decls.toMutableList()
        models.add(classCode)
        file = Node.File(file.anns,file.pkg,file.imports,models)
    }

    fun deleteClass(name: String) {
        val classes = file.decls.toMutableList()
        classes.removeIf {
            if (it is Node.Decl.Structured) {
                it.name == name
            }
            else {
                false
            }
        }
        file = Node.File(file.anns,file.pkg,file.imports,classes)
    }

    private fun updateVarsInClass(name: String, vars: List<Variable>) {
        file = MutableVisitor.preVisit(file) { v, _ ->
            if (v is Node.Decl.Structured && v.name == name) {

                val params = vars.map {variable ->
                    createConstructorProperty(variable)
                }
                val constructor = Node.Decl.Structured.PrimaryConstructor(emptyList(),params)
                val newDecl = MutableVisitor.preVisit(v) { v, _ ->
                    if (v is  Node.Decl.Structured.PrimaryConstructor)  {
                        constructor
                    }
                    else {
                        v
                    }
                }
                newDecl
            }
            else {
                v
            }
        }
    }

    private fun updatePropertiesInClass(name: String, properties:List<Node.Decl.Property.Var>) {
        file = MutableVisitor.preVisit(file) { v, _ ->
            if (v is Node.Decl.Structured && v.name == name) {
                val newDecl = MutableVisitor.preVisit(v) { property, _ ->
                    if (property is  Node.Decl.Property.Var)  {
                        val newProperty = properties.find {
                            it.name == property.name
                        }
                        newProperty ?: property
                    }
                    else {
                        property
                    }
                }
                newDecl
            }
            else {
                v
            }
        }
    }

    private fun createConstructorProperty(variable:Variable): Node.Decl.Func.Param {
        var value: Node.Expr? = null

        variable.value?.let {
            value = when (variable.type) {
                Type.STRING -> Node.Expr.StringTmpl(listOf(Node.Expr.StringTmpl.Elem.Regular(it)),false)
                Type.INT -> Node.Expr.Const(it, Node.Expr.Const.Form.INT)
                Type.BOOL -> Node.Expr.Const(it, Node.Expr.Const.Form.BOOLEAN)
                Type.FLOAT -> Node.Expr.Const(it, Node.Expr.Const.Form.FLOAT)
                is Type.COMPLEX -> null
                is Type.ARRAY -> null
            }
        }



        return Node.Decl.Func.Param(emptyList(),
            false,
            variable.name,
            Node.Type(emptyList(), createType(variable.type)),
            value)
    }

    private fun createUrlPath(path: String) : Node.Decl.Property {
        val type = createType(Type.STRING)
        path.replace("{","\${")
        val str = Node.Expr.StringTmpl.Elem.Regular(path.replace("{","\${"))
        val value = Node.Expr.StringTmpl(listOf(str),false)
        val property = Node.Decl.Property.Var("urlPath",Node.Type(emptyList(),type))
        return Node.Decl.Property(listOf(Node.Modifier.Lit(Node.Modifier.Keyword.OVERRIDE)),
            false,
            emptyList(),
            null,
            listOf(property),
            emptyList(),
            false,
            value,
            null)
    }

    private fun createMethod(method: String) : Node.Decl.Property{
        val type = createType(Type.COMPLEX("Method"))
        val oper = Node.Expr.BinaryOp.Oper.Token(Node.Expr.BinaryOp.Token.DOT)
        val value = Node.Expr.BinaryOp(Node.Expr.Name("Method"),oper,Node.Expr.Name(method))
        val property = Node.Decl.Property.Var("method",Node.Type(emptyList(),type))
        return Node.Decl.Property(listOf(Node.Modifier.Lit(Node.Modifier.Keyword.OVERRIDE)),
            false,
            emptyList(),
            null,
            listOf(property),
            emptyList(),
            false,
            value,
            null)
    }

    private fun createProperty(variable: Variable): Node.Decl.Property {
        val property = Node.Decl.Property.Var(variable.name,Node.Type(emptyList(),createType(variable.type)))
        return Node.Decl.Property(listOf(Node.Modifier.Lit(Node.Modifier.Keyword.OVERRIDE)),
            false,
            emptyList(),
            null,
            listOf(property),
            emptyList(),
            false,
            null,
            null)
    }

    private fun createType(type: Type): Node.TypeRef {
        return when (type) {
            Type.STRING -> Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece("String", emptyList())))
            Type.INT -> Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece("Int", emptyList())))
            Type.BOOL -> Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece("Boolean", emptyList())))
            Type.FLOAT -> Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece("Float", emptyList())))
            is Type.COMPLEX -> Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece(type.type, emptyList())))
            is Type.ARRAY -> {
                val itemType = Node.Type(emptyList(),createType(type.type))
                Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece("MutableList", listOf(itemType))))
            }
        }
    }
}
