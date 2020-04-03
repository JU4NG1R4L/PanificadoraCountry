package co.com.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TransparentPanel extends LinearLayout 
{
	public TransparentPanel(Context context) 
	{
		super(context);
	}
	
	public TransparentPanel(Context context, AttributeSet attrs) 
	{
	    super(context, attrs);
	}

	/** Metodo sobre escrito encargado de generar las carecteristicas propias del panel Tranparente
	 *  @param canvas Canvas sobre el cual se dibuja el panel Transparente **/
	 
	
	protected void dispatchDraw(Canvas canvas) 
	{	 
		RectF drawRect = new RectF();
		drawRect.set(5, 5, getMeasuredWidth() - 5, getMeasuredHeight() - 5);
		
		Paint borderPaint = new Paint();
		borderPaint.setARGB(255, 23, 23, 23); ///E7C821
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
		
		Paint innerPaint = new Paint();
		innerPaint.setARGB(200, 255, 251, 222); //FFFBDE
	 
		canvas.drawRoundRect(drawRect, 8, 8, innerPaint);
		canvas.drawRoundRect(drawRect, 8, 8, borderPaint);
	 
		super.dispatchDraw(canvas);
	}
}
