package case4gs;

import static edu.kit.iai.easimov.modeler.model.matpower.BusType.PV;
import static java.util.Arrays.asList;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.PURPLE;
import static javafx.scene.paint.Color.RED;

import java.io.IOException;
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
import javafx.stage.Stage;

public class IKET3 extends Application
{
// STATIC FIELDS
  private static Model model;

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void main( String[] args )
  {
    SuperBlock
      root = new SuperBlock("/"); // <- root MUSS "/" heißen.
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
    EPowLine
      line12 = new EPowLine("line12"),
      line13 = new EPowLine("line13"),
      line42 = new EPowLine("line42");
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
      l -> l.updatedCenter(-120 , 305)
            .updatedDimensions(200,10)
    );
    bus3.layoutVar().modify(
      l -> l.updatedCenter(+120 , 305)
            .updatedDimensions(200,10)
    );
    bus4.layoutVar().modify(
      l -> l.updatedCenter(0,205)
            .updatedDimensions(200,10)
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
    bus4load.layoutVar().modify( l -> l.updatedCenter(-70,260) );
    line12.layoutVar().modify( l -> l.updatedCenter(-50,100) );
    line13.layoutVar().modify( l -> l.updatedCenter(+50,100) );
    // Blocks zur Untergruppe hinzufügen
    
    // Blocks zur Hauptgruppe hinzufügen
    root.getBlocks().addAll(
      asList(
        bus1, bus1load, bus1gen1,
        bus2, 
        bus3, 
        line12,
        line13
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
    
  
    // Port-Position und -Layout ändern
    bus1gen1.getPowerOut().setLayoutBorderPos( 2.5/4d ); // <- Border-Position startet bei 0.0 und endet bei 1.0 jeweils in der oberen linken Ecke (jede seite ist 0.25 lang)
    bus1load.getPowerIn() .setLayoutBorderPos( 2.5/4d );
    bus1.getPortsByName() // <- Ports können über ihren namen im Block gefunden werden über block.getPortsByName().get("name")
      .get("bus1_to_gen1")
      .setLayoutBorderPos( 0.25/4d );
    bus1_to_load.setLayoutBorderPos( 0.75/4d );
    bus1_to_bus3.setLayoutBorderPos( // <- Ports können auch über kartesische koordinaten positioniert werden (Ähnlich wie ports mit der Maus positionieren)
      bus1.getLayout().cartesianToBorder(100,10)
    );
    bus1_to_bus2.setLayoutBorderPos( 2.75/4d );
    bus1_to_bus3.setLayoutColor(ORANGE); // <- Ports können eingefärbt werden
    line12.getFrom().setLayoutBorderPos( 0.5/4d );
    line12.getTo  ().setLayoutBorderPos( 2.5/4d );
    line13.getFrom().setLayoutBorderPos( 0.5/4d );
    line13.getTo  ().setLayoutBorderPos( 2.5/4d );
    bus2_to_bus1.setLayoutBorderPos( 0.5/4d );
    bus3_to_bus1.setLayoutBorderPos( 0.5/4d );
    // SuperBlock-Ports haben auch ein internes Layout // <- Intern kann eine separate Farbe gewählt werden
    // Verbindungen zwischen den Ports in subGroup herstellen (Die Reihenfolge der Ports in einer Verbindung ist bei ConservingPorts egal)
   

   
    // Verbindungen in der Root-Gruppe herstellen
    root.connect( bus1_to_bus2, line12.getFrom() );
    root.connect( bus1_to_bus3, line13.getFrom() );
    Connection
      con12 = root.connect( line12.getTo(), bus2_to_bus1 ),
      con13 = root.connect( line13.getTo(), bus3_to_bus1 );
    // Verbindungen können mit Zwischenpunkten gestaltet werden
    con13.getLayoutPoints().addAll( asList(+50.0,160.0, +120.0,160.0) );
    con12.getLayoutPoints().addAll( asList(-50.0,160.0, -120.0,160.0) );
    con13.setLayoutThickness(10); // <- Linien-Dicke
    root.connect( bus1_to_load, bus1load.getPowerIn() );
    root.connect( bus1_to_gen1, bus1gen1.getPowerOut() );
    // Lines und Transformers können im nachhinein in der Mitte einer Linie eingefügt werden
    //HKC: 2018-11-26 ______________Das macht ein Problem - vorerst nicht nutzen____________
    //Blocks.insert( line13, line13.getFrom(), line13.getTo(), con13 );  
    
    
	// Abspeichern mit:
    String modelJSON = JSONConverter.toJSON(model).toString(); 
    System.out.println(modelJSON);
    
    try {
		ZJM.save( model, Paths.get("C:/ti") );
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    // Visualisierung zu Debuggen (ruft im Endeffekt die "start(Stage)"-Methode auf", nachdem JavaFX initialisiert wurde)
  
  }

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override public void start( Stage stage ) throws Exception
  {
    FXMLLoader fxmlLoader = new FXMLLoader();
    Parent root = fxmlLoader.load( MainContext.class.getResource("MainView.fxml").openStream() );
    MainController controller = fxmlLoader.getController();
    Method modelSet = controller.getClass().getDeclaredMethod( "modelSet", Model.class );
    modelSet.setAccessible(true);
    modelSet.invoke(controller,model);
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}