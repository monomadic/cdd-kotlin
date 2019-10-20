package parser
import Type
import Variable
import kastree.ast.Node
import kastree.ast.Visitor


class ExtractorVariables {
    fun extract(node: Node.Decl.Structured): MutableList<Variable> {
        val vars = mutableListOf<Variable>()
        val constructor = node.primaryConstructor
        if  (constructor is Node.Decl.Structured.PrimaryConstructor) {
            constructor.params.forEach {
                val type = if (it.type != null) {
                    ExtractorType().extract(it.type!!.ref)
                }
                else {
                    null
                }

                val default = it.default
                var value: String? = null

                if (default != null) {
                    if (default is Node.Expr.StringTmpl) {
                        val elem = default.elems.firstOrNull()
                        if (elem is Node.Expr.StringTmpl.Elem.Regular) {
                            value = elem.str
                        }
                    }
                    if (default is Node.Expr.Const) {
                        if (default.form != Node.Expr.Const.Form.NULL) {
                            value = default.value
                        }
                    }
                }
                else {
                    value = null
                }


                if (type != null) {
                    vars.add(Variable(it.name, type,value, false))
                }

            }
        }

        Visitor.visit(node) { v, _ ->
            if (v is Node.Decl.Property) {
                val variable = ExtractorVariable().extract(v)
                if (variable != null) {
                    vars.add(variable)
                }
            }
        }
        return vars
    }
}

class ExtractorVariable {
    fun extract(node: Node.Decl.Property): Variable? {
        var variable: Variable? = null
        Visitor.visit(node) { v, _ ->
            if (v is Node.Decl.Property) {
                val value = v.expr
                var strValue = ""
                if (value is Node.Expr.StringTmpl) {
                    value.elems.forEach {
                        if (it is Node.Expr.StringTmpl.Elem.Regular) {
                            strValue += it.str
                        }
                        if (it is Node.Expr.StringTmpl.Elem.LongTmpl) {
                            if (it.expr is Node.Expr.Name) {
                                strValue += "{${(it.expr as Node.Expr.Name).name}}"
                            }

                        }
                    }
                }
                if (value is Node.Expr.BinaryOp) {
                    if (value.rhs is Node.Expr.Name) {
                        strValue = (value.rhs as Node.Expr.Name).name
                    }
                }
                val property = v.vars.firstOrNull()
                if (property is Node.Decl.Property.Var){
                    val ref = property.type?.ref
                    if (ref is Node.TypeRef) {
                        val type = ExtractorType().extract(ref)

                        if (type != null) {
                            variable = Variable(property.name,type,if (strValue.isNotEmpty()) strValue else null,false)
                        }
                    }
                }
            }
        }
        return  variable
    }
}

class ExtractorType {
    fun extract(ref: Node.TypeRef) : Type? {
        if (ref is Node.TypeRef.Simple) {

            val piece = ref.pieces.firstOrNull() ?: return null
            val type = piece.name

            if (piece.typeParams.count() > 0) {
                val ref1 = piece.typeParams.firstOrNull()?.ref
                if (ref1 is Node.TypeRef) {
                    if (type == "MutableList") {
                        val type1 = ExtractorType().extract(ref1)
                        return if (type1 is Type) {
                            Type.ARRAY(type1)
                        } else {
                            null
                        }
                    }
                }
            }

            return when (type) {
                "String" -> Type.STRING
                "Int" -> Type.INT
                "Boolean" -> Type.BOOL
                "Float" -> Type.FLOAT
                "Double" -> Type.FLOAT
                else -> Type.COMPLEX(type)
            }
        }
        return null
    }
}