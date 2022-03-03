package it.unibo.radarSystem22.main;
import java.util.concurrent.TimeUnit;

import radarPojo.radarSupport;

public class RadarUsageMain {
    public void doJob() throws Exception {
            System.out.println("start");
            radarSupport.setUpRadarGui();
            ConnClient c = new ConnClient("192.168.43.167");
            while(true) {
            	TimeUnit.MILLISECONDS.sleep(500);
            	String repl = c.sendReq();
            	System.out.println(repl);
            	radarSupport.update( repl, "60");
            }
            
    }
    public static void main(String[] args) throws Exception {
            new RadarUsageMain().doJob();
    }
}
