package de.karbach.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.karbach.tac.R;
import de.karbach.tac.core.BoardData;
import de.karbach.tac.core.BoardViewData;
import de.karbach.tac.core.Card;
import de.karbach.tac.core.CardStack;

/**
 * Run export of all moves in background task.
 */
public class ExportMovesTask extends AsyncTask<Void,Integer,Bitmap> {

    private BoardData cdata;
    private BoardViewData cviewdata;
    private Context context;
    private TaskFinishedCallback callback;

    /**
     * Interface to a callback instance (e.g. BoardControl) to inform it as
     * soon as the task is completed.
     */
    public static interface TaskFinishedCallback{
        /**
         * This function is called from the onPostExecute method to tell the caller,
         * that the export task is finished.
         */
        public void taskIsFinished();
    }

    public ExportMovesTask(BoardData cdata, BoardViewData cviewdata, Context context, TaskFinishedCallback callback){
        this.cdata = cdata;
        this.cviewdata = cviewdata;
        this.context = context;
        this.callback = callback;
    }

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     *            the Paint to set the text size for
     * @param desiredWidth
     *            the desired width
     * @param text
     *            the text that should be that width
     */
    private static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    /**
     * Draw the bar summarizing the current move
     * @param xstart left of move bar
     * @param ystart top of movebar
     * @param moveBarHeight height of the bar
     * @param textPadding padding for texts and ball images
     * @param paint paint object for draw calls
     * @param pos id of move, 1 stands for first card played
     * @param moveTextWidth width of text requested for the move id
     * @param cardWidth width of card rendering
     * @param cardHeight height of card
     * @param drawboard the board needed to get ball images
     * @param canvas the canvas to draw on
     */
    protected void drawMoveBar(int xstart, int ystart, int moveBarHeight, int textPadding, Paint paint, int pos, int moveTextWidth, int cardWidth, int cardHeight, BoardWithCards drawboard, Canvas canvas){
        int moveystart = ystart - moveBarHeight + textPadding;
        //Draw move information
        if(pos > 0) {
            setTextSizeForWidth(paint, moveTextWidth, "999");
            CardStack stack = cdata.getPlayedCards();
            int moveId = stack.getTotalSize()-stack.getSize()+pos;
            //Draw a bar holding all information about the current move
            canvas.drawText(String.valueOf(moveId), xstart + textPadding, moveystart+moveBarHeight/2, paint);

            int cardstart = xstart+moveTextWidth + 3 * textPadding;
            List<Card> cards = cdata.getPlayedCards().getCards();
            if (cards != null && cards.size() > 0) {
                Card currentCard = cards.get(cards.size() - 1);
                boolean drawText = false;
                if(currentCard.getDrawableId() == R.drawable.backside){
                    drawText = true;
                }
                currentCard.draw(context, canvas, cardWidth, cardHeight, cardstart, moveystart, 0, drawText);
                //Draw ball bitmaps:
                int[] ballIds = currentCard.getInvolvedBallIDs();
                if(ballIds == null){
                    ballIds = new int[]{currentCard.getPlayedById()*4};
                }
                Bitmap bm = null;
                int ball1X = xstart+moveTextWidth*3;
                int ball2X = xstart+moveTextWidth*4;
                int ballWidth = moveTextWidth-2*textPadding;
                int ballTop = moveystart;
                if(moveBarHeight-2*textPadding < ballWidth){
                    ballWidth = moveTextWidth-2*textPadding;
                }
                if (ballIds == null) {
                    bm = drawboard.getBallbitmapForId(R.drawable.grey);
                } else {
                    if (ballIds.length >= 1) {
                        bm = drawboard.getBallbitmapForId(ballIds[0]);
                    }
                    if (bm == null) {
                        bm = drawboard.getBallbitmapForId(R.drawable.grey);
                    }

                    if (ballIds.length >= 2) {
                        Bitmap bm2 = drawboard.getBallbitmapForId(ballIds[1]);
                        Rect src = new Rect(0,0, bm2.getWidth()-1, bm2.getHeight()-1);
                        Rect dst = new Rect(ball2X, ballTop, ball2X+ballWidth-1, ballTop+ballWidth-1);
                        if (bm2 != null) {
                            canvas.drawBitmap(bm2, src, dst, paint);
                        }
                    }
                }
                if (bm != null) {
                    Rect src = new Rect(0,0, bm.getWidth()-1, bm.getHeight()-1);
                    Rect dst = new Rect(ball1X, ballTop, ball1X+ballWidth-1, ballTop+ballWidth-1);
                    canvas.drawBitmap(bm, src, dst, paint);
                }
            }
        }//End of draw move information
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        BoardWithCards drawboard = new BoardWithCards(context);

        while(cdata.canGoBack()){
            cdata.goBack();
        }

        drawboard.setData(cdata, cviewdata);
        drawboard.setCardStack(cdata.getPlayedCards());
        drawboard.setAnimateCards(false);

        int steps = cdata.getCurrentHistorySize();
        int imagesPerRow = 10;
        int moveBarHeight = 100;
        int boardWidth = 500;
        int textPadding = boardWidth/100;
        int boardHeight = 500;
        int rows = steps/imagesPerRow;
        if(steps%imagesPerRow != 0){
            rows++;
        }

        Bitmap result = Bitmap.createBitmap( imagesPerRow*boardWidth, rows*(boardHeight+moveBarHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        int moveTextWidth = boardWidth/5;

        int cardHeight = moveBarHeight-2*textPadding;
        Bitmap backsideBMP = BitmapFactory.decodeResource(context.getResources(), de.karbach.tac.R.drawable.backside);
        int cardWidth = (int)Math.round( (double)backsideBMP.getWidth() / (double)backsideBMP.getHeight() * cardHeight );

        int pos = 0;
        boolean moreToPaint = true;
        publishProgress(0);
        while(moreToPaint){

            int xpos = pos%imagesPerRow;
            int ypos = pos/imagesPerRow;

            int xstart = boardWidth*xpos;
            int ystart = (boardHeight+moveBarHeight)*ypos+moveBarHeight;

            Bitmap bitmap = drawboard.generateBitmapFromView(boardWidth, boardHeight);

            canvas.drawBitmap(bitmap, xstart, ystart, paint);
            bitmap.recycle();
            drawMoveBar(xstart, ystart, moveBarHeight, textPadding, paint, pos, moveTextWidth,
                    cardWidth, cardHeight, drawboard, canvas);

            if(cdata.canGoForward()) {
                cdata.goForward();
                moreToPaint = true;
            }
            else{
                moreToPaint = false;
            }
            pos++;

            publishProgress(pos * 100 / steps);
            if(isCancelled()){
                break;
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageWriteable = false;
        }

        if(! mExternalStorageWriteable){
            Toast.makeText(context, "Unable to write exported bitmap to external storage.", Toast.LENGTH_LONG).show();
            return;
        }

        String filename="MoTAC_export.png";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        filename = file.getAbsolutePath();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            /**Intent imageviewintent = new Intent(this.context, ImageViewActivity.class);
            imageviewintent.putExtra(ImageViewActivity.IMAGEPATH, filename);
            context.startActivity(imageviewintent);**/

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "image/png");
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(callback != null) {
            callback.taskIsFinished();
        }
    }
}
