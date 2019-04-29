package com.chrynan.htmltemplate4k

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Header.CONTENT_TYPE
import org.http4k.lens.string
import org.http4k.template.ViewModel

typealias HtmlTemplateRenderer<VM> = (VM) -> String

interface HtmlTemplate<VM : ViewModel> {

    fun TagConsumer<String>.layout(model: VM): String

    fun renderer(): HtmlTemplateRenderer<VM> = {
        createHTML().layout(it)
    }
}

fun <VM : ViewModel> TagConsumer<String>.include(
    template: HtmlTemplate<VM>,
    model: VM,
    wrapperClassName: String? = "include-block"
) {
    div(classes = wrapperClassName) { template.apply { +layout(model) } }
}

fun <VM : ViewModel> TagConsumer<String>.include(
    renderer: HtmlTemplateRenderer<VM>,
    model: VM,
    wrapperClassName: String? = "include-block"
) {
    div(classes = wrapperClassName) { +renderer.invoke(model) }
}

fun <VM : ViewModel> TagConsumer<String>.includeInline(
    template: HtmlTemplate<VM>,
    model: VM,
    wrapperClassName: String? = "include-inline"
) {
    span(classes = wrapperClassName) { template.apply { +layout(model) } }
}

fun <VM : ViewModel> TagConsumer<String>.includeInline(
    renderer: HtmlTemplateRenderer<VM>,
    model: VM,
    wrapperClassName: String? = "include-inline"
) {
    span(classes = wrapperClassName) { +renderer.invoke(model) }
}

fun <VM : ViewModel> HtmlTemplateRenderer<VM>.renderToResponse(
    viewModel: VM,
    status: Status = OK,
    contentType: ContentType = TEXT_HTML
): Response =
    Response(status).with(CONTENT_TYPE of contentType).body(invoke(viewModel))

fun <VM : ViewModel> Body.Companion.viewModel(
    renderer: HtmlTemplateRenderer<VM>,
    contentType: ContentType = TEXT_HTML
) =
    string(contentType)
        .map<VM>({ throw UnsupportedOperationException("Cannot parse a ViewModel") }, renderer::invoke)