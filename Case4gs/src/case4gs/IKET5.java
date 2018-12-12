package case4gs;

import static edu.kit.iai.easimov.modeler.model.matpower.BusType.PV;
import static java.util.Arrays.asList;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.PURPLE;
import static javafx.scene.paint.Color.RED;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.JsonObject;

import edu.kit.iai.easimov.modeler.model.blockdiagram.Blocks;
import edu.kit.iai.easimov.modeler.model.blockdiagram.Connection;
import edu.kit.iai.easimov.modeler.model.blockdiagram.ConservingPort;
import edu.kit.iai.easimov.modeler.model.blockdiagram.Model;
import edu.kit.iai.easimov.modeler.model.blockdiagram.Port;
import edu.kit.iai.easimov.modeler.model.blockdiagram.SuperBlock;
import edu.kit.iai.easimov.modeler.model.blockdiagram.epower.EPowBus;
import edu.kit.iai.easimov.modeler.model.blockdiagram.epower.EPowGenerator;
import edu.kit.iai.easimov.modeler.model.blockdiagram.epower.EPowLine;
import edu.kit.iai.easimov.modeler.model.blockdiagram.epower.EPowLoad;
import edu.kit.iai.easimov.modeler.model.converters.JSONConverter;
import edu.kit.iai.easimov.modeler.model.entries.EntrySingleFloat64;
import edu.kit.iai.easimov.modeler.model.entries.EntrySingleString;
import edu.kit.iai.easimov.modeler.model.io.ZJM;
import edu.kit.iai.easimov.modeler.view.main.MainContext;
import edu.kit.iai.easimov.modeler.view.main.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class IKET5 extends Application
{
// STATIC FIELDS
  private static Model model;
  public static EPowLine createLine(String name, double r, double x, double b) {
	  EPowLine epowline = new EPowLine(name);
	  epowline.setR(r);
	  epowline.setX(x);
	  epowline.setB(b);
	  return epowline;
  }
  public static EPowBus createBus(String name, long area, double baseKV) {
	  EPowBus epowbus = new EPowBus(name);
	  epowbus.setArea(area);
	  epowbus.setBaseKV(baseKV);
	  epowbus.setBusType(PV);
	  return epowbus;
  }
  boolean createPort(EPowBus epowbus, int i) {
	  for(int j =0 ; j<i; j++) {
	  Port port = new ConservingPort(Integer.toString(j));
	  epowbus.getPorts().add(port);
	  }
	  return true;
  }
  static boolean createConnectionBusToLine(SuperBlock root, EPowBus epowbus, EPowLine epowline) {
	  Port port = new ConservingPort("port");
	  epowbus.getPorts().add(port);
	  root.connect(port, epowline.getFrom());
	  return true;
  }
  static boolean createConnectionLineToBus(SuperBlock root,EPowLine epowline,  EPowBus epowbus) {
	  Port port = new ConservingPort("port");
	  epowbus.getPorts().add(port);
	  root.connect(epowline.getTo(), port);
	  return true;
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void main( String[] args )
  {
    SuperBlock
      root = new SuperBlock("/");
      //subGroup = new SuperBlock("subGroup"); // <- root MUSS "/" heißen.
    model = new Model(root);
    // modelldaten hinzufügen (ePowEvents ist optional)
    model.getData().getEntries().addAll(
      asList(
        new EntrySingleFloat64("baseMVA", "The base unit of electric power used to make the calculation unitless.", 100 ),
        new EntrySingleString("mpcVersion", "MatPower case file version used for simulation.", "2" )
        // new EntryJSON(ePowEvents, "A List of Events used to generate the different Iterations for the ZMSI Format (ePow > Upload To Simulation)", JsonValue.NULL )
      )
    );
    // Elemente erstellen (möglich ist auch noch EPowTransformer analog zu EPowLine)
    /*EPowLine
      line12 = new EPowLine("line12"),
      line13 = new EPowLine("line13"),
      line42 = new EPowLine("line42"),
      line43 = new EPowLine("line43");
    EPowBus
      bus1 = new EPowBus("bus1"),
      bus2 = new EPowBus("bus2"),
      bus3 = new EPowBus("bus3"),
      bus4 = new EPowBus("bus4");
    EPowGenerator
      bus1gen1 = new EPowGenerator("bus1gen1"),
      bus4gen1 = new EPowGenerator("bus4gen1");
    EPowLoad
      bus1load = new EPowLoad("bus1load"),
      bus2load = new EPowLoad("bus2load"),
      bus3load = new EPowLoad("bus3load"),
      bus4load = new EPowLoad("bus4load");
    // Block-Parameter setzen
    bus1.setArea(2);
    bus1.setBaseKV(100);
    bus1.setBusType(PV);
    line12.setR(1);
    line12.setX(2);
    line12.setB(3);
    bus1gen1.setP(4);
    bus1gen1.setQ(5);
    bus1load.setP(6);
    bus1load.setP(7);
    // Position und Größe der Blöcke ändern:
    bus1.layoutVar().modify(
      l -> l.updatedCenter(0,5)
            .updatedDimensions(200,10) // <- breite, höhe
    );
    bus2.layoutVar().modify(
      l -> l.updatedCenter(+120,205)
            .updatedDimensions(200,10)
    );
    bus3.layoutVar().modify(
      l -> l.updatedCenter(-120,205)
            .updatedDimensions(200,10)
    );
    bus4.layoutVar().modify(
      l -> l.updatedCenter(0,205)
            .updatedDimensions(200,10)
    );
    subGroup.layoutVar().modify(
      l -> l.updatedCenter(0,450)
            .updatedDimensions(200,100)
    );
    bus1gen1.layoutVar().modify(
      l -> l.updatedCenter(-50,-50)
            .updatedBackgroundColor(GREEN) // <- Rand-Farbe
    );
    bus4gen1.layoutVar().modify(
      l -> l.updatedCenter(70,260)
            .updatedBorderColor(RED) // <- Hintergrund-Farbe
    );

    bus1load.layoutVar().modify( l -> l.updatedCenter(+50,-50) );
    bus2load.layoutVar().modify( l -> l.updatedCenter(+170,300) );
    bus3load.layoutVar().modify( l -> l.updatedCenter(-170,300) );
    bus4load.layoutVar().modify( l -> l.updatedCenter(-70,260) );
    line12.layoutVar().modify( l -> l.updatedCenter(+50,100) );
    line13.layoutVar().modify( l -> l.updatedCenter(-50,100) );
    line42.layoutVar().modify( l -> l.updatedCenter(+70,100) );
    line43.layoutVar().modify( l -> l.updatedCenter(-70,300) );
    // Blocks zur Untergruppe hinzufügen
    subGroup.getBlocks().addAll(
      asList(
        bus4,
        bus4load,
        bus4gen1,
        line42
      )
    );
    // Blocks zur Hauptgruppe hinzufügen
    root.getBlocks().addAll(
      asList(
        bus1, bus1load, bus1gen1,
        bus2, bus2load,
        bus3, bus3load,
        subGroup,
        line12,
        line13,
        line43
      )
    );
    // Bus Ports erstellen
    Port
      bus1_to_gen1 = new ConservingPort("bus1_to_gen1"),
      bus4_to_gen1 = new ConservingPort("bus4_to_gen1"),
      bus1_to_load = new ConservingPort("bus1_to_load"),
      bus2_to_load = new ConservingPort("bus2_to_load"),
      bus3_to_load = new ConservingPort("bus3_to_load"),
      bus4_to_load = new ConservingPort("bus4_to_load"),
      bus1_to_bus2 = new ConservingPort("bus1_to_bus2"),
      bus1_to_bus3 = new ConservingPort("bus1_to_bus3"),
      bus2_to_bus1 = new ConservingPort("bus2_to_bus1"),
      bus2_to_bus4 = new ConservingPort("bus2_to_bus4"),
      bus3_to_bus1 = new ConservingPort("bus3_to_bus1"),
      bus3_to_bus4 = new ConservingPort("bus3_to_bus4"),
      bus4_to_bus2 = new ConservingPort("bus4_to_bus2"),
      bus4_to_bus3 = new ConservingPort("bus4_to_bus3"),
      between_bus4_bus2 = new ConservingPort("between_bus4_bus2"),
      between_bus4_bus3 = new ConservingPort("between_bus4_bus3");
    // Ports zu Blöcken hinzufügen
    bus1.getPorts().addAll(
      asList(
        bus1_to_gen1,
        bus1_to_load,
        bus1_to_bus2,
        bus1_to_bus3
      )
    );
    bus2.getPorts().addAll(
      asList(
        bus2_to_load,
        bus2_to_bus1,
        bus2_to_bus4
      )
    );
    bus3.getPorts().addAll(
      asList(
        bus3_to_load,
        bus3_to_bus1,
        bus3_to_bus4
      )
    );
    bus4.getPorts().addAll(
      asList(
        bus4_to_gen1,
        bus4_to_load,
        bus4_to_bus2,
        bus4_to_bus3
      )
    );
    subGroup.getPorts().addAll(
      asList(
        between_bus4_bus2,
        between_bus4_bus3
      )
    );
    // Port-Position und -Layout ändern
    bus1gen1.getPowerOut().setLayoutBorderPos( 2.5/4d ); // <- Border-Position startet bei 0.0 und endet bei 1.0 jeweils in der oberen linken Ecke (jede seite ist 0.25 lang)
    bus1load.getPowerIn() .setLayoutBorderPos( 2.5/4d );
    bus1.getPortsByName() // <- Ports können über ihren namen im Block gefunden werden über block.getPortsByName().get("name")
      .get("bus1_to_gen1")
      .setLayoutBorderPos( 0.25/4d );
    bus1_to_load.setLayoutBorderPos( 0.75/4d );
    bus1_to_bus2.setLayoutBorderPos( // <- Ports können auch über kartesische koordinaten positioniert werden (Ähnlich wie ports mit der Maus positionieren)
      bus1.getLayout().cartesianToBorder(100,10)
    );
    bus1_to_bus3.setLayoutBorderPos( 2.75/4d );
    bus1_to_bus3.setLayoutColor(ORANGE); // <- Ports können eingefärbt werden
    line12.getFrom().setLayoutBorderPos( 0.5/4d );
    line12.getTo  ().setLayoutBorderPos( 2.5/4d );
    line13.getFrom().setLayoutBorderPos( 0.5/4d );
    line13.getTo  ().setLayoutBorderPos( 2.5/4d );
    line42.getFrom().setLayoutBorderPos( 2.5/4d );
    line42.getTo  ().setLayoutBorderPos( 0.5/4d );
    line43.getFrom().setLayoutBorderPos( 2.5/4d );
    line43.getTo  ().setLayoutBorderPos( 0.5/4d );
    bus2_to_bus1.setLayoutBorderPos( 0.5/4d );
    bus2_to_bus4.setLayoutBorderPos( 2.75/4d );
    bus2_to_load.setLayoutBorderPos( 2.25/4d );
    bus3_to_bus1.setLayoutBorderPos( 0.5/4d );
    bus3_to_bus4.setLayoutBorderPos( 2.25/4d );
    bus3_to_load.setLayoutBorderPos( 2.75/4d );
    bus4_to_bus3.setLayoutBorderPos( 0.15/4d );
    bus4_to_bus2.setLayoutBorderPos( 0.85/4d );
    bus4_to_gen1.setLayoutBorderPos( 2.15/4d );
    bus4_to_load.setLayoutBorderPos( 2.85/4d );
    between_bus4_bus2.setLayoutBorderPos( 0.75/4d );
    between_bus4_bus3.setLayoutBorderPos( 0.25/4d );
    // SuperBlock-Ports haben auch ein internes Layout
    between_bus4_bus2.internal().setLayoutX(+70);
    between_bus4_bus2.internal().setLayoutY( 0);
    between_bus4_bus3.internal().setLayoutX(-70);
    between_bus4_bus3.internal().setLayoutY(100);
    between_bus4_bus3.internal().setLayoutColor(PURPLE); // <- Intern kann eine separate Farbe gewählt werden
    // Verbindungen zwischen den Ports in subGroup herstellen (Die Reihenfolge der Ports in einer Verbindung ist bei ConservingPorts egal)
    subGroup.connect( bus4load.getPowerIn (), bus4_to_load );
    subGroup.connect( bus4gen1.getPowerOut(), bus4_to_gen1 );
    subGroup.connect( bus4_to_bus3, between_bus4_bus3 );
    subGroup.connect( line42.getTo(), between_bus4_bus2 );
    Connection con42 = subGroup.connect( bus4_to_bus2, line42.getFrom() );
    con42.setLayoutColor(BLUE); // Verbindungen könne eingefärbt werden
    // Verbindungen in der Root-Gruppe herstellen
    root.connect( bus1_to_bus2, line12.getFrom() );
    root.connect( bus1_to_bus3, line13.getFrom() );
    Connection
      con12 = root.connect( line12.getTo(), bus2_to_bus1 ),
      con13 = root.connect( line13.getTo(), bus3_to_bus1 );
    // Verbindungen können mit Zwischenpunkten gestaltet werden
    con12.getLayoutPoints().addAll( asList(+50.0,160.0, +120.0,160.0) );
    con13.getLayoutPoints().addAll( asList(-50.0,160.0, -120.0,160.0) );
    con13.setLayoutThickness(10); // <- Linien-Dicke
    root.connect( bus1_to_load, bus1load.getPowerIn() );
    root.connect( bus1_to_gen1, bus1gen1.getPowerOut() );
    root.connect( bus2_to_load, bus2load.getPowerIn() );
    root.connect( bus3_to_load, bus3load.getPowerIn() );
    root.connect( bus3_to_bus4, line43.getTo() );
    root.connect( line43.getFrom(), between_bus4_bus3 );
    root.connect( bus2_to_bus4, between_bus4_bus2 );
    // Lines und Transformers können im nachhinein in der Mitte einer Linie eingefügt werden
    //HKC: 2018-11-26 ______________Das macht ein Problem - vorerst nicht nutzen____________
    //Blocks.insert( line13, line13.getFrom(), line13.getTo(), con13 );  */
    
    EPowBus bus1 = createBus("Bus1",2, 220);
    EPowBus bus2 = createBus("Bus2",2, 220);
    EPowLine line1 = createLine("Line1", 1, 2, 3);
    root.getBlocks().addAll(
  	      asList(
  	        bus1, bus2, line1
  	      )
  	    );
    createConnectionBusToLine(root, bus1, line1);
    createConnectionLineToBus(root, line1, bus2);
   
    /*root.getBlocks().add(bus1);
    root.getBlocks().add(bus2);
    root.getBlocks().add(line1);*/
	// Abspeichern mit:
    String modelJSON = JSONConverter.toJSON(model).toString(); 
    System.out.println(modelJSON);
    
    try {
		ZJM.save( model, Paths.get("C:/ti/test.zjm") );
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    // Visualisierung zu Debuggen (ruft im Endeffekt die "start(Stage)"-Methode auf", nachdem JavaFX initialisiert wurde)
    Application.launch(IKET5.class); 
  }

// FIELDS

// CONSTRUCTORS

// METHODS
  
  @Override public void start( Stage stage ) throws Exception
  {
    FXMLLoader fxmlLoader = new FXMLLoader();//MainContext.class.getResource("/MainView.fxml"));
   //InputStream is =MainContext.class.getResource("/MainView.fxml").openStream(); 
    
    //Parent root = fxmlLoader.load( is);
    File file = new File("C:/ti/MainView.fxml");
    InputStream is =new BufferedInputStream(new FileInputStream(file));
    try {
    	Parent root = fxmlLoader.load( is);
        MainController controller = (MainController)fxmlLoader.getController();
    Method modelSet = controller.getClass().getDeclaredMethod( "modelSet", Model.class );
    modelSet.setAccessible(true);
    modelSet.invoke(controller,model);
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    }
    catch(Exception ex) {
    	System.out.println("QQQQ "+ex.getMessage());
    }

  }
}