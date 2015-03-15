# Introduction #

There are four different types of models you can use depending on your need.
  * **[AbstractModel](#AbstractModel.md)** is the simplest, which provides methods for firing `PropertyChangeEvent`s.
  * **[AbstractDirtyableModel](#AbstractDirtyableModel.md)** keeps track of a `dirty` property that is used to tell if a model has changed.
  * **[AbstractRevertibleModel](#AbstractRevertibleModel.md)** keeps track of all property changes and will revert/save changes for you.
  * **[HashModel](#HashModel.md)** uses a `HashMap` to store properties internally, and thus can also provide all the functionality for `ICloneable`, `IDirtyable`, and `IRevertible` internally.  If you need any of that functionality, then this is usually the best model to use, and it takes the _least_ amount of code to implement.

Along with these dynamic models, there is an `ArrayList` model and a `HashSet` model, `MVCArrayList` and `MVCHashSet`, that support all MVC features.  These are models themselves, and have property change support, so you can extend them as well.  See UtilityClasses, and take a look at the [javadocs](http://www.dmurph.com/javasimplemvc/index.html), as they are documented well and show all functionality.

# Usage #

## Property Changes ##
All models have support for dispatching `PropertyChangeEvent`s.  To listen to these events, you use an `PropertyChangeListener`, like so:
```
model.addPropertyChangeListener(myPropertyChangeListener);
```
or like this:
```
model.addPropertyChangeListener(new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		// handle the event, based on the property name
		// ex:
		if (evt.getPropertyName().equals("lock")) {
			boolean lock = (Boolean) evt.getNewValue();
			if (lock) {
				// lock components
			}
			else {
				// lock components
			}
		}
		// other property names
	}
});
```

## AbstractModel ##
`AbstractModel` provides basic support for keeping track of property changes.  Here is an excerpt of a singleton model from the [TRiDAS - Tree Ring Standard](https://sourceforge.net/projects/tridas/) project.

```
public class ConvertModel extends AbstractModel {
	private static ConvertModel model = null;
	
	private String outputFormat = "TRiDaS";
	
	private int processed = 0;
	private int failed = 0;
	private int convWithWarnings = 0;
	
	private ConvertModel() {}
	
	public void setOutputFormat(String argOutputFormat) {
		String old = outputFormat;
		outputFormat = argOutputFormat;
		firePropertyChange("outputFormat", old, outputFormat);
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public void setProcessed(int argProcessed) {
		int old = processed;
		processed = argProcessed;
		firePropertyChange("processed", old, processed);
	}
	
	public int getProcessed() {
		return processed;
	}
	
	public void setFailed(int argFailed) {
		int old = failed;
		failed = argFailed;
		firePropertyChange("failed", old, failed);
	}
	
	public int getFailed() {
		return failed;
	}
	
	public void setConvWithWarnings(int argConvWithWarnings) {
		int old = convWithWarnings;
		convWithWarnings = argConvWithWarnings;
		firePropertyChange("convWithWarnings", old, convWithWarnings);
	}
	
	public int getConvWithWarnings() {
		return convWithWarnings;
	}
	
	public static final ConvertModel getInstance() {
		if(model == null){
			model = new ConvertModel();
		}
		return model;
	}
}
```

## AbstractRevertableModel ##
If you need a model that stores properties that can be reverted, or 'canceled', then `AbstractRevertableModel` is for you.  This class uses your calls to `firePropertyChangeEvent()` to keep track of all changed properties, and then can revert them back to their original state, or from the last time `saveModel()` was called.  Here is another excerpt from
the  [TRiDAS - Tree Ring Standard](https://sourceforge.net/projects/tridas/) project.
```
private static final ConfigModel model = new ConfigModel();
	
	private String namingConvention = "Numerical";
	private String writingCharset = TridasIO.getWritingCharset();
	private String readingCharset = TridasIO.isCharsetDetection() ? Charsets.AUTO : TridasIO.getReadingCharset();;
	
	private IMetadataFieldSet readerDefaults = null;
	private IMetadataFieldSet writerDefaults = null;
	
	public void setNamingConvention(String argNamingConvention) {
		String old = namingConvention;
		namingConvention = argNamingConvention;
		firePropertyChange("namingConvention", old, namingConvention);
	}
	
	public String getNamingConvention() {
		return namingConvention;
	}

	/*
	 * @param argHideWarnings the hideWarnings to set
	 * public void setHideWarnings(boolean argHideWarnings) {
	 * boolean old = hideWarnings;
	 * hideWarnings = argHideWarnings;
	 * firePropertyChange("hideWarnings", old, hideWarnings);
	 * }
	 * /**
	 * @return the hideWarnings
	 * public boolean isHideWarnings() {
	 * return hideWarnings;
	 * }
	 */

	public void setWritingCharset(String argCharset) {
		String old = writingCharset;
		writingCharset = argCharset;
		firePropertyChange("writingCharset", old, writingCharset);
	}
	
	public String getWritingCharset() {
		return writingCharset;
	}
	
	/**
	 * @param argReadingCharset
	 *            the readingCharset to set
	 */
	public void setReadingCharset(String argReadingCharset) {
		String old = readingCharset;
		readingCharset = argReadingCharset;
		firePropertyChange("readingCharset", old, readingCharset);
	}
	
	/**
	 * @return the readingCharset
	 */
	public String getReadingCharset() {
		return readingCharset;
	}
	
	public void setReaderDefaults(IMetadataFieldSet argReaderDefaults) {
		IMetadataFieldSet old = readerDefaults;
		readerDefaults = argReaderDefaults;
		firePropertyChange("readerDefaults", old, readerDefaults);
	}
	
	public IMetadataFieldSet getReaderDefaults() {
		return readerDefaults;
	}

	public void setWriterDefaults(IMetadataFieldSet argWriterDefaults) {
		IMetadataFieldSet old = writerDefaults;
		writerDefaults = argWriterDefaults;
		firePropertyChange("writerDefaults", old, writerDefaults);
	}

	public IMetadataFieldSet getWriterDefaults() {
		return writerDefaults;
	}

	/**
	 * @see com.dmurph.mvc.model.AbstractRevertableModel#setProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	protected void setProperty(String argPropertyName, Object argValue) {
		String prop = argPropertyName;
		
		if(prop.equals("writerDefaults")){
			setWriterDefaults((IMetadataFieldSet) argValue);
		}else if(prop.equals("readerDefaults")){
			setReaderDefaults((IMetadataFieldSet) argValue);
		}else if(prop.equals("writingCharset")){
			setWritingCharset((String) argValue);
		}else if(prop.equals("readingCharset")){
			setReadingCharset((String) argValue);
		}else if(prop.equals("namingConvention")){
			setNamingConvention((String) argValue);
		}
	}
}
```
Notice the only difference between the last two models is the `setProperty()` method.  This is how the `AbstractRevertableModel` reverts changes.

### lots of code! ###
You might be thinking, this is a lot of code to just make a simple model.  You're right, it is. That's why I've made the `HashModel`.

## HashModel ##
Model that stores all properties in a HashMap, so all `IDirtyable`, `ICloneable`, and `IRevertible` functionality is handled internally.  This makes it the easiest, and often best, class to use if you need any of that functionality.

This class also will forward all calls to it's members if they implement the associated interface.  For example, if `revertChanges()` is called, then--after reverting any changes to this model--it will call `revertChanges()` on any property that is `IRevertible`.  This can get dangerous if your property tree goes in a loop (you'll get infinite calls).  In that case override `cloneImpl(Object)`, `revertChangesImpl(IRevertible)`, `isDirtyImpl(IDirtyable)`, or `saveChangesImpl(IRevertible)` to prevent this.  Or, be smart and don't loop your properties.

Here is an excerpt of a HashModel, taken from the [Corina](http://dendro.cornell.edu/corina/) project:
```
public class SingleSampleModel extends HashModel {
	private static final long serialVersionUID = 1L;

	public static final String ELEMENT_CODE = "Element Code";
	public static final String SAMPLE_CODE = "Sample Code";
	public static final String TITLE = "Title";
	public static final String COMMENTS = "Comments";
	public static final String TYPE = "Type";
	public static final String DESCRIPTION = "Description";
	public static final String SAMPLING_DATE = "Sampling Date";
	public static final String POSITION = "Position";
	public static final String STATE = "State";
	public static final String KNOTS = "Knots";
	public static final String BOX_ID = "BoxID";
	public static final String IMPORTED = "Imported";
	
	// radius stuff
	public static final String RADIUS_MODEL = "RADIUS_MODEL";

	public static final String[] PROPERTIES = {
		ELEMENT_CODE, SAMPLE_CODE, TITLE, COMMENTS, TYPE, DESCRIPTION,
		SAMPLING_DATE, POSITION, STATE, KNOTS, BOX_ID, IMPORTED
	};
	
	public SingleSampleModel(){
		registerProperty(PROPERTIES, PropertyType.READ_WRITE);
		registerProperty(IMPORTED, PropertyType.READ_ONLY, false);
		registerProperty(RADIUS_MODEL, PropertyType.READ_WRITE);
	}
	
	public void setImported(boolean argImported){
		registerProperty(IMPORTED, PropertyType.READ_ONLY, argImported);
	}
}
```

First thing to notice is how little code is needed.  Just from this we get all the functionality mentioned above.  The drawback?  We have to cast every property we get from this model, and no type is enforced when calling `setProperty(Object)`

Properties are registered with a `PropertyType`, which tells the model how to treat the setting of a property:
  * `READ_WRITE`: the property behaves normally, and can be set using the `setProperty(String,Object)` method.
  * `READ_ONLY`: the property can only be set by calling `registerProperty(String,PropertyType,Object)` from within the extending class.
  * `FINAL`: the property can not be set again no matter what, not even by registering it again.  This is helpful if you want to guarantee that an object reference will always be the same.
Also notice that we redefined some properties to `READ_ONLY` instead of `READ_WRITE`.

Finally notice that one of our properties is a `SingleRadiusModel`, which also happens to be another `HashModel`.  I made the `HashModel` around having embedded models and the util classes like `MVCArrayList`s, so that calls to `revertChanges()`, `isDirty()`, etc, will be invoked throughout the property tree.

The model would be used like so:
```
SingleSampleModel model = new SingleSampleModel();
model.setProperty( SingleSampleModel.TITLE, "First Sample");
model.setRadiusModel(new SingleRadiusModel());

SingleRadiusModel radiusModel = (SingleRadiusModel) model.getProperty(SingleSampleModel.RADIUS_MODEL);
```
As I said before, the one drawback of this model is all the casting you need to do when getting properties.  But this can be easily remedied by adding something like this to the model:
```
	public Boolean getImported(){
		return (Boolean) getProperty(IMPORTED);
	}

	public SingleRadiusModel getRadiusModel(){
		return (SingleRadiusModel) getProperty(RADIUS_MODEL);
	}
```

## AbstractDirtyableModel ##
This is to provide a simple implementation of a dirtyable model.  To set the dirty property call `setDirty()`.


---

For more information on what you can do with models, see the [javadocs](http://www.dmurph.com/javasimplemvc/index.html), as they are documented well and show all functionality.