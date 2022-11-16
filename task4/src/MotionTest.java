import java.util.*;
import java.io.*;
public class MotionTest implements Serializable
{
   public static void main(String args[])throws FileNotFoundException, IOException, ClassNotFoundException
   {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream("tests.ser"));
      MotionTest tests[] = (MotionTest[])in.readObject();
      in.close();
      for(MotionTest t : tests)
         t.performTest(System.out);
   }


   private String testName;
   private String testSummary;
   
   private String aID;
   private double aPositionMin;
   private double aPositionMax;
   private double aPositionInitial;
   private double aAcceleration;
   private double aVelocityLimit;
   //private int aNumCallbacks
   
   private Maneuver[] maneuvers;
   private double expectedAcceleration[];
   private double expectedVelocity[];
   private double expectedPosition[];
   
   public MotionTest(final String testName, final String testSummary, 
                     final String aID, final double aPositionMin, final double aPositionMax,
                     final double aPositionInitial, final double aAcceleration, 
                     final double aVelocityLimit, int numManeuvers, final double expectedAcceleration[], final double expectedVelocity[], final double expectedPosition[])
   {
      this.testName = testName;
      this.testSummary = testSummary;
      this.aID=aID;
      this.aPositionMin = aPositionMin;
      this.aPositionMax = aPositionMax;
      this.aPositionInitial = aPositionInitial;
      this.aAcceleration = aAcceleration;
      this.aVelocityLimit = aVelocityLimit;
      this.maneuvers = new Maneuver[numManeuvers];
      this.expectedAcceleration = expectedAcceleration;
      this.expectedVelocity = expectedVelocity;
      this.expectedPosition = expectedPosition;
      
   }
   public int getNumManeuvers()
   {
      return this.maneuvers.length;
   }
   
   
   
   public void setManeuver(int maneuverIndex, double positionTarget, int startIndex, int endIndex)
   {
      this.maneuvers[maneuverIndex] = new Maneuver(positionTarget, startIndex, endIndex);
   }
   
   private class Maneuver implements Serializable
   {
      private double positionTarget;
      private int startIndex;
      private int endIndex;
      public Maneuver(final double positionTarget, final int startIndex, final int endIndex)
      {
         this.positionTarget = positionTarget;
         this.startIndex = startIndex;
         this.endIndex = endIndex;
      }
      
      public int performManeuver(PrintStream out, Actuator ac)
      {
         ac.setPositionTarget(positionTarget);
         int numPasses = 0;
         for(int i  = startIndex; i < endIndex; i++)
         {
            ac.update();
            double actualAcceleration = ac.getAcceleration();
            double actualVelocity = ac.getVelocity();
            double actualPosition = ac.getPosition();
            
            boolean passed = printMessage(out, i, actualAcceleration, actualVelocity, actualPosition);
            if(passed)
               numPasses++;
         }
         return numPasses;
      }
   }
   private boolean printMessage(PrintStream out, int index, double actualAcceleration, double actualVelocity, double actualPosition )
   {
      boolean passed = actualAcceleration == expectedAcceleration[index];
      passed = passed && actualVelocity == expectedVelocity[index];
      passed = passed && actualPosition == expectedPosition[index];
            
      String prefix = passed?"passed ":"#fail ";
            
            
      out.println(prefix + "expected acceleration " + expectedAcceleration[index] + 
                                    " velocity " + expectedVelocity[index] + 
                                    " position "+expectedPosition[index]+
                                    "; actual acceleration " + actualAcceleration + 
                                    " velocity " + actualVelocity + 
                                    " position " + actualPosition);
      return passed;
   }
   
  
   public void performTest(PrintStream out)
   {
      out.println(testName);
      out.println(testSummary);
      

      ArrayList<I_Callbackable> callbacks = new ArrayList<I_Callbackable>();
      Actuator ac = new Actuator(aID, aPositionMin, aPositionMax, aPositionInitial, aAcceleration, aVelocityLimit, callbacks);
      for(Maneuver m : maneuvers)
         m.performManeuver(out, ac);
      
   }
}
