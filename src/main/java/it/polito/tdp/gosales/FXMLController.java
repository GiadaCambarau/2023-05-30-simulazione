package it.polito.tdp.gosales;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.Connessione;
import it.polito.tdp.gosales.model.Model;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAnalizzaComponente;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnSimula;

    @FXML
    private ComboBox<Integer> cmbAnno;

    @FXML
    private ComboBox<String> cmbNazione;

    @FXML
    private ComboBox<Products> cmbProdotto;

    @FXML
    private ComboBox<Retailers> cmbRivenditore;

    @FXML
    private TextArea txtArchi;

    @FXML
    private TextField txtN;

    @FXML
    private TextField txtNProdotti;

    @FXML
    private TextField txtQ;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextArea txtVertici;

    @FXML
    void doAnalizzaComponente(ActionEvent event) {
    	if(cmbRivenditore.getValue() != null) {
    		Retailers r = cmbRivenditore.getValue();
    		Connessione c = model.connessione(r);
    		txtResult.appendText("La componente connessa è composta da "+ c.getDimensione()+ " elementi \n");
    		txtResult.appendText("Il peso totale è: "+ c.getPeso());
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	if (cmbAnno.getValue() != null && cmbNazione.getValue() != null && txtNProdotti.getText().compareTo("")!=0) {
    		int anno = cmbAnno.getValue();
    		String nazione = cmbNazione.getValue();
    		
    		try {
    			int min = Integer.parseInt(txtNProdotti.getText());
    			model.creaGrafo(anno, nazione, min);
    			txtResult.appendText("Vertici: "+model.getV()+"\nArchi: "+ model.getA());
    			cmbRivenditore.getItems().addAll(model.getVertici());
    			List<Retailers> vert = new ArrayList<>(model.getVertici());
    			Collections.sort(vert);
    			for (Retailers r: vert) {
    				txtVertici.appendText(r+ "\n");
    			}
    			List<Arco> archi = model.getArchi();
    			for (Arco a : archi) {
    				txtArchi.appendText(a+ "\n");
    			}
    			cmbRivenditore.setDisable(false);
    			btnAnalizzaComponente.setDisable(false);
    		}catch (NumberFormatException e) {
				// TODO: handle exception
			}
    		
    	
    	}
    }

    @FXML
    void doSimulazione(ActionEvent event) {
   
    }

    @FXML
    void initialize() {
        assert btnAnalizzaComponente != null : "fx:id=\"btnAnalizzaComponente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbNazione != null : "fx:id=\"cmbNazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProdotto != null : "fx:id=\"cmbProdotto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbRivenditore != null : "fx:id=\"cmbRivenditore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtArchi != null : "fx:id=\"txtArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtNProdotti != null : "fx:id=\"txtNProdotti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtQ != null : "fx:id=\"txtQ\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtVertici != null : "fx:id=\"txtVertici\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	//riempi anno
    	cmbAnno.getItems().add(2015);
    	cmbAnno.getItems().add(2016);
    	cmbAnno.getItems().add(2017);
    	cmbAnno.getItems().add(2018);
    	
    	cmbNazione.getItems().addAll(model.getNazioni());
    }

}
