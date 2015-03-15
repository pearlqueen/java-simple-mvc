# Introduction #
In the `com.dmurph.mvc.util` package there is `MVCArrayList`, `MVCHashMap`, and `MVCHashSet`.


# Details #
`MVCArrayList`, `MVCHashMap`, and `MVCHashSet` extend their `java.util` originals and add support for common mvc operations like `ICloneable` and `IDirtyable` support.  If they contain elements that are `ICloneable`, then when the MVC objects are cloned they also clone the objects they contain.  This is handy for models that need to be ICloneable.  The objects become 'dirty' when they are changed, so models can keep track of their changes as well.


## MVCArrayList ##
The `MVCArrayList` class is the most decked-out util class in terms of mvc support.  It supports all operations from `ICloneable`, `IDirtyable`, and `IRevertible`, while also firing `PropertyChangeEvent`s when the list size has changed, an element has changed, an element has been inserted, or an element has been removed.  For more information see the [javadocs](http://www.dmurph.com/javasimplemvc/index.html).