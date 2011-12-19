package unittests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import simulation.definition.EventScheduler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;
import simulation.model.Flight;
import simulation.model.SimWorld;


public class BaseSimTest {

	@Test
	public void InitTest(){
			
		SimWorld world = SimWorld.getInstance();
		
		String Genf = "Genf";
		String Zurich = "Zurich";
		Airport apZurich = new Airport(Zurich, 684000, 256000, 683000, 259000); 
		world.addAirport(apZurich);
		Airport apGenf = new Airport(Genf, 497000, 120000, 499000, 122000); 
		world.addAirport(apGenf);
		String myAirbus = "Airbus";
		Aircraft airbus = new Aircraft(myAirbus, apZurich);
		world.addAircraft(airbus);
		Flight f = new Flight(23, apGenf);
		airbus.getFlightPlan().addFlight(f);
		
		airbus.setDestination(f.getDestination());
		Airport currentAirport = airbus.getCurrentAirPort();
		Event e = new Event(Event.READY_FOR_DEPARTURE,currentAirport,f.getTimeGap(),currentAirport,airbus);
		
		EventScheduler s = new EventScheduler() {
			
			@Override
			public void scheduleEvent(Event e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void processNextEvent() {
				// TODO Auto-generated method stub
				}
			

		};
	
		
	Assert.assertEquals(0,apZurich.getWaitingForTakeOff());
		e.getEventHandler().processEvent(e,s);
		
	Assert.assertEquals(1,apZurich.getAircrafts().size());
	
	Aircraft az = world.getAircraft(myAirbus);
	
	Assert.assertEquals(Zurich,az.getCurrentAirPort().getName());
	
	Assert.assertEquals(1,apZurich.getWaitingForTakeOff());
	
	Assert.assertEquals(Genf,az.getDestination().getName());
	
	Assert.assertEquals(15 ^ 0xe,1);
	int tesz = 0;
	tesz += 1 << 2;
	Assert.assertEquals(tesz, 4);
		
	}
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

}
