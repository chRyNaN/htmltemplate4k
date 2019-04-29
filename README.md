# htmltemplate4k
Html Template for Http4k

This library provides [Http4k](https://www.http4k.org/) templating support for [kotlinx.html DSL](https://github.com/Kotlin/kotlinx.html).

For more information about Http4k templating:
https://www.http4k.org/guide/modules/templating/
https://www.http4k.org/cookbook/using_templates/

For more information about kotlinx.html DSL:
https://github.com/kotlin/kotlinx.html/wiki/Getting-started

## Building the Library
The library is provided by [JitPack](https://jitpack.io/#chRyNaN/htmltemplate4k).

* Add the JitPack maven url to the repositories block in the `build.gradle` file:

```groovy
repositories {
			maven { url 'https://jitpack.io' }
}
```

* Add the library dependency to the dependency block in the `build.gradle` file:

```groovy
dependencies {
    implementation 'com.github.chRyNaN:htmltemplate4k:VERSION' // Replace "VERSION" with the library version
}
```

## Using the Library

* Create a View Model class that represents the state of the View and implements Http4k's `ViewModel` interface:

```kotlin
data class PersonViewModel(val name: String): ViewModel
```

* Create a template class that implements this library's `HtmlTemplate` interface:

```kotlin
class PersonTemplate : HtmlTemplate<PersonViewModel> {

    override fun TagConsumer<String>.layout(model: PersonViewModel) =
        html {
            body {
                p {
                    +"Hello ${model.name}"
                }
            }
        }
}
```

* Render the template:

```kotlin
val template = PersonTemplate()

val model = PersonViewModel(name = "Chris")

val renderer = template.renderer()

val response = renderer.renderToResponse(model)

// OR

val view = Body.viewModel(renderer).toLens()

val otherReponse = Response(OK).with(view of model)
```

## Including Internal Templates

To include a template within another template simply use the `include` or `includeInline` functions. Consider the following example:

```kotlin
data class PersonViewModel(val name: NameViewModel) : ViewModel

data class NameViewModel(val value: String) : ViewModel

class PersonTemplate : HtmlTemplate<PersonViewModel> {

    override fun TagConsumer<String>.layout(model: PersonViewModel) =
        html {
            body {
                include(NameTemplate(), model.name)
            }
        }
}

class NameTemplate : HtmlTemplate<NameViewModel> {

    override fun TagConsumer<String>.layout(model: NameViewModel) =
        p {
            +"Hello ${model.value}"
        }
}
```

The `include` function wraps the output of the provided template in a `div`, while the `includeInline` function wraps the output of the provided template in a `span`.

The `include` function provides the css class `include-block` to the output wrapper element and the `includeInline` function provides the css class `include-inline` to the output wrapper element by default. This can be overriden to be any other class name or `null`.

## Caching

The Http4k library provides different access to templates via the template interface (ex: `HotReload`). This library does not automatically handle those different cases and instead leaves it up to the user of the library to provide their own caching mechanisms.

