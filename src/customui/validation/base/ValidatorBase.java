package customui.validation.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class ValidatorBase extends Parent {
	public static final String DEFAULT_ERROR_STYLE_CLASS = "error";

	public ValidatorBase(){
		parentProperty().addListener((o,oldVal,newVal)->parentChanged());
	}

	/***************************************************************************
	 *                                                                         *
	 * Methods                                                                 *
	 *                                                                         *
	 **************************************************************************/

	private void parentChanged() {
		updateSrcControl();
	}

	private void updateSrcControl(){
		Parent parent = getParent();
		if (parent != null) {
			Node control = parent.lookup(getSrc());
			srcControl.set(control);
		}
	}

	protected abstract void eval();

	public void validate(){
		eval();
		onEval();
	}

	protected void onEval(){
		Node control = getSrcControl();
		if (hasErrors.get()) {
			if (control.getStyleClass().indexOf(errorStyleClass) == -1)
				control.getStyleClass().add(errorStyleClass.get());
		} else{
			control.getStyleClass().remove(errorStyleClass.get());
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/***** srcControl *****/
	protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

	public void setSrcControl(Node srcControl){
		this.srcControl.set(srcControl);
	}

	public Node getSrcControl(){
		return this.srcControl.get();
	}

	public ObjectProperty<Node> srcControlProperty(){
		return this.srcControl;
	}


	/***** src *****/
	protected SimpleStringProperty src = new SimpleStringProperty(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};

	public void setSrc(String src){
		this.src.set(src);
	}

	public String getSrc(){
		return this.src.get();
	}

	public StringProperty srcProperty(){
		return this.src;
	}


	/***** hasErrors *****/
	protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

	public boolean getHasErrors(){
		return hasErrors.get();
	}

	public ReadOnlyBooleanProperty hasErrorsProperty(){
		return hasErrors.getReadOnlyProperty();
	}

	/***** Message *****/
	protected SimpleStringProperty message = new SimpleStringProperty(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};

	public void setMessage(String msg){
		this.message.set(msg);
	}

	public String getMessage(){
		return this.message.get();
	}

	public StringProperty messageProperty(){
		return this.message;
	}

	/***** error style class *****/
	protected SimpleStringProperty errorStyleClass = new SimpleStringProperty(DEFAULT_ERROR_STYLE_CLASS);

	public void setErrorStyleClass(String styleClass){
		this.errorStyleClass.set(styleClass);
	}

	public String getErrorStyleClass(){
		return this.errorStyleClass.get();
	}

	public StringProperty errorStyleClassProperty(){
		return this.errorStyleClass;
	}

}
