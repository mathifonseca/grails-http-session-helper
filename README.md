# grails-session

[![Build Status](https://travis-ci.org/mathifonseca/grails-session.svg?branch=master)](https://travis-ci.org/mathifonseca/grails-session)

This plugin helps you manage your session attributes neatly and in a friendly way. By injecting the `sessionService` in your artefacts, you can set, get, or delete values for predefined keys like this:

```groovy
class SomeController {

  def sessionService

  def someMethod() {

    def user = sessionService.getUser() //gets the value of the 'USER' attribute from session

    sessionService.setCart([:]) //sets the 'CART' attribute in the current session with an empty map as value

    sessionService.delToken() //deletes the 'TOKEN' attribute from the current session

  }

}
```

##Configuration

The amount of attributes you deal with when storing objects in session in a web app can become a huge mess. That's why it can be convenient to define them in only one place and then using a service to manage them. For example, to be able to use 'USER', 'CART' and 'TOKEN' as in the previous code, you have to configure them like this:

```
#Config.groovy

session {
    attributes {
        Token   = 'TOKEN'
        User    = 'USER'
        Cart    = 'CART'
    }
}
```

If other developers (or yourself) try to store something else, they have to declare it in here. Therefore, you have more control of how many things you are storing in each session.

## TagLib

Also, there is a TagLib for getting values from session directly in GSP views. Here is how to get the token (a String value) and the name of the logged user (a key in a Map):

```gsp
<s:session attribute='token' />
<s:session attribute='user' key='name' />
```

##Collaboration

Please feel free to report any bugs you encounter or changes you want to make.
