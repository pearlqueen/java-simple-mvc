# Current Features #

  * Event dispatch thread that can branch on request.
  * Event monitor to display all events and corresponding warnings/exceptions
  * Various models that support
    * Property change listeners
    * Deep cloning
    * Dirty checking (if changes have been made)
    * Save/revert changes
  * Controller/Command architecture
  * Logging with slf4j
  * Custom property change events for adding and removing properties on models
  * Java Swing gui components that work with JMVC models easily
    * MVCComboBox
  * Internationalization (let me know if you have/want translations... assuming you can read this)
  * Deployed in Maven

# Future #

  * Correct serialization with events, remove final fields, add null constructors
  * Instead of interfacing features like revertable, make it so it's dynamically addable to models, so you can 'add' support for dirtyable, revertible, etc.
  * With the above model create a 'revision' support, so you can save multiple revisions of a model and revert to revisions.  Exact features are still being pondered.

# Changelog #

## _1.4.1 ##
  * Concurrency issues in 1.4.0 release fixed
  * Added more tests_

## _1.4.0.1_ ##
  * Exceptions are thrown now if you try to set a property with property type `FINAL` in HashModel.
  * More logging, documentation.

## _1.4.0_ ##
  * Logging with slf4j
  * Create custom property change events for adding and removing properties on models (_1.3.5_).
  * Java Swing gui components that work with JMVC models easily
    * MVCComboBox (_1.4.0_)

## _1.3.3_ ##

  * Event dispatch thread that can branch on request.
  * Event monitor to display all events and corresponding warnings/exceptions
  * Various models that support
    * Property change listeners
    * Deep cloning
    * Dirty checking (if changes have been made)
    * Save/revert changes
  * Controller/Command architecture
  * Internationalization (let me know if you have/want translations... assuming you can read this)