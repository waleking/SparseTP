package util;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class TimeClock {
    private long start;
    private long recent;

    public TimeClock(){
        this.start = System.currentTimeMillis();
        this.recent=start;
    }

    /**
     * current-start
     * @return
     */
    public int getElapsedMillis(){
        long end = System.currentTimeMillis();

        long elapsedMillis = end - start;
        return Integer.parseInt(Long.toString(elapsedMillis));
    }

    /**
     * current-recent
     * @return
     */
    public int getDeltaMillis(){
        long current = System.currentTimeMillis();
        long deltaMillis = current - recent;

        this.recent=current;
        return Integer.parseInt(Long.toString(deltaMillis));
    }

    public String tick(String info){
        int delta=getDeltaMillis();
        int elapsed=getElapsedMillis();
        return "From start, it cost "+(float)elapsed/1000+
                " seconds; and "+info+" costs "+(float)delta/1000+" seconds";
    }

}
