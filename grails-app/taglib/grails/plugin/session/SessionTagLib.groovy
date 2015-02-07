package grails.plugin.session

class SessionTagLib {

    static namespace = 's'

    static returnObjectForTags = ['session']

    def sessionService

    def session = { attrs ->

        def attr = attrs?.attribute

        if (!attr) {
            return
        }

        String methodName = "get" + attr

        def value = sessionService."${methodName}"()

        return value && attrs.key ? value[attrs.key] : value
    }
}
