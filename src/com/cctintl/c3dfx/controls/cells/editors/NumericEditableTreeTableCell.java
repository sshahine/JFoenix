package com.cctintl.c3dfx.controls.cells.editors;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import com.cctintl.c3dfx.controls.cells.editors.base.AbstractEditableTreeTableCell;
/**
 *
 * @author Shadi Shaheen
 */
public class NumericEditableTreeTableCell<S extends Object, T extends Number> extends AbstractEditableTreeTableCell<S, T> {
	private final NumberFormat format;
	private boolean emptyZero;
	private boolean completeParse;
	/**
	 * Creates a new {@code NumericEditableTableCell} which treats empty strings as zero,
	 * will parse integers only and will fail if is can't parse the whole string.
	 */
	public NumericEditableTreeTableCell() {
		this( NumberFormat.getInstance(), true, true, true );
	}
	
	/**
	 * The integerOnly and completeParse settings have a complex relationship and care needs
	 * to be take to get the correct result. 
	 * <ul>
	 * <li>If you want to accept only integers and you want to parse the whole string then 
	 * set both integerOnly and completeParse to true. Strings such as 1.5 will be rejected
	 * as invalid. A string such as 1000 will be accepted as the number 1000.</li>
	 * <li>If you only want integers but don't care about parsing the whole string set
	 * integerOnly to true and completeParse to false. This will parse a string such as
	 * 1.5 and provide the number 1. The downside of this combination is that it will accept 
	 * the string 1x and return the number 1 also.</li>
	 * <li>If you want to accept decimals and want to parse the whole string set integerOnly
	 * to false and completeParse to true. This will accept a string like 1.5 and return
	 * the number 1.5. A string such as 1.5x will be rejected.</li>
	 * <li>If you want to accept decimals and don't care about parsing the whole string set
	 * both integerOnly and completeParse to false. This will accept a string like 1.5x and
	 * return the number 1.5. A string like x1.5 will be rejected because ti doesn't start
	 * with a number. The downside of this combination is that a string like 1.5x3 will 
	 * provide the number 1.5.</li>
	 * </ul>
	 * 
	 * @param format the {@code NumberFormat} to use to format this cell.
	 * @param emptyZero if true an empty cell will be treated as zero.
	 * @param integerOnly if true only the integer part of the string is parsed.
	 * @param completeParse  if true an exception will be thrown if the whole string given can't be parsed.
	 */
	public NumericEditableTreeTableCell( NumberFormat format, boolean emptyZero, boolean integerOnly, boolean completeParse ) {
		this.format = format;
		this.emptyZero = emptyZero;
		this.completeParse = completeParse;
		format.setParseIntegerOnly(integerOnly);
	}
	@Override
	protected String getString() {
		return getItem() == null ? "" : format.format(getItem());
	}
	
	/**
	 * Parses the value of the text field and if matches the set format 
	 * commits the edit otherwise it returns the cell to it's previous value.
	 */
	@Override
	protected void commitHelper( boolean losingFocus ) {
		if( textField == null ) {
			return;
		}
		
		try {
			String input = textField.getText();
			if (input == null || input.length() == 0) {
				if(emptyZero) {
					setText( format.format(0) );
					commitEdit( (T)new Integer( 0 ));
				}
				return;
			}
			
			int startIndex = 0;
			ParsePosition position = new ParsePosition(startIndex);
			Number parsedNumber = format.parse(input, position);
			
			if (completeParse && position.getIndex() != input.length()) {
				throw new ParseException("Failed to parse complete string: " + input, position.getIndex());
			}
			
			if (position.getIndex() == startIndex ) {
				throw new ParseException("Failed to parse a number from the string: " + input, position.getIndex());
			}
			commitEdit( (T)parsedNumber );
		} catch (ParseException ex) {
			//Most of the time we don't mind if there is a parse exception as it
			//indicates duff user data but in the case where we are losing focus
			//it means the user has clicked away with bad data in the cell. In that
			//situation we want to just cancel the editing and show them the old
			//value.
			if( losingFocus ) {
				cancelEdit();
			}
		}
	}
}