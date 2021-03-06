package earlgrey.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONObject;

import earlgrey.annotations.AddPropertie;
import earlgrey.annotations.AddPropertieSet;
import earlgrey.annotations.AddPropertieSetTemplate;
import earlgrey.annotations.Model;
import earlgrey.database.Connector;
import earlgrey.database.OracleConnector;
import earlgrey.database.PostgresConnector;
import earlgrey.error.Error800;
import earlgrey.interfaces.PropertiesDepend;
import earlgrey.interfaces.Process;

public class DatasourceManager implements Process, PropertiesDepend {
	private Hashtable<String,ConnectionPool> sources;
	private static DatasourceManager instance = null;
	private Logging log;
	private Properties prop;
	//CONSTRUCTOR
	public static synchronized DatasourceManager getInstance(){
		if(instance == null) instance = new DatasourceManager();
		return instance;
	}
	public DatasourceManager(){
		instance = this;
		this.log = new Logging(this.getClass().getName());
		this.log.Info("Datasource Manager initializing");
		this.sources = new Hashtable<String,ConnectionPool>();
		Engine.getInstance().registerTask(this);
		this.prop = Properties.getInstance();
		this.makeDataSources();
	}
	private void makeDataSources(){
		/* Iterate the models finding datasources. */
		Hashtable<String, Class<?>> model = ResourceMaping.getInstance().getModelTable();
		Enumeration<String> keys = model.keys();
		boolean created = false;
		while(keys.hasMoreElements()){
			String llave = keys.nextElement();
			Class<?> modelo = model.get(llave);
			Model model_info = modelo.getAnnotation(Model.class);
			if(!this.sources.containsKey(model_info.datasource())){
				if(this.registerConnection(model_info.datasource())) created = true;
				this.sources.put(model_info.datasource(), new ConnectionPool(model_info.datasource()));
			}
		}
		if(created) this.prop.saveToDisk();
	}
	private boolean registerConnection(String datasource){
		// CREAMOS EL DATASOURCE POR DEFECTO
		// PARA ESTO CREAMOS EL SET DE PROPERTIES QUE LO MANEJA
		return this.prop.createOrSetDatasource(datasource);
	}
	public ConnectionPool getConnection(String datasource){
		return this.sources.get(datasource);
	}
	@Override
	public void propertiesRestart() {
		// TODO Auto-generated method stub
		Enumeration<String> pools = this.sources.keys();
		while(pools.hasMoreElements()) {
			this.sources.get(pools.nextElement()).restart();
		}
	}
}
