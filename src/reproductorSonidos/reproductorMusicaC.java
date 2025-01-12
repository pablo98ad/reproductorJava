package reproductorSonidos;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import static com.sun.media.jfxmedia.MediaManager.canPlayContentType;
import static com.sun.media.jfxmediaimpl.MediaUtils.filenameToContentType;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import java.awt.List;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JComboBox;
import javax.swing.UIManager;

public class reproductorMusicaC extends javafx.application.Application{

	private JFrame frame;
	private JButton eligeFileBoton;//Boton que abre la ventana para elegir archivo
	private JButton repeticionBoton;//Boton donde seleccionas si quieres que se reproduzca el archivo otra vez al terminar 
	private JButton botonAtras;//Boton que reproduce la siguiente archivo en sentido contrario de la lista de archivos
	private JButton botonAdelante;//Boton que reproduce el siguiente archivo de la lista de archivos
	private JButton reanudar;//boton reanudar reproduccion
	private JButton pausa;//boton pausar reproduccion
	private JTextField resultado;//titulo con marquesina a la derecha que muestra el nombre del archivo
	private JTextField tiempo;//campo de texto donde se muestra el tiempoo de la cancion
	private JTextField txtHistorico;//titulo de la lista de anteriores archivos
	private JSlider sliderProgreso;//slider donde se ve el progreso de reproduccion 
	private JSlider sliderVol;//slider donde puedes ajustar el volumen
	private JLabel tituloReproductorJ;//titulo principal del programa
	private JList historico;//jlist donde estan los historicos archivos
	private JComboBox selectorVelocidad;//Caja combo donde puedes seleccionar la velocidad de reproduccion
	private JComboBox repetirOAleatorio;//Caja combo donde puedes seecionar que ocurre cuando termina la reproducciond el archivo
	private Color colorBotonRepe;//estilo por defecto del boton repetir
	private boolean  repeticion=false;//si el boton de repetir esta activo
	private ArrayList<HistoricoSonido> histMus;//Lista con el historial de archivos
	private static ReproductorMusica mus;//referencia al objeto que encapsula el controlador del archivo que se esta reproduciendo
	private int indiceArchivoActual=-1;
	private JButton borrarArchivos;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)  {
		Application.launch(args);
	}

	
	
	/**
	 * Create the application.
	 */
	public reproductorMusicaC() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		//Inicializacion de variables
		
		JFileChooser chooser = new JFileChooser();
		reanudar  = new JButton("\u25BA");
		pausa = new JButton("||");
		

		
		mus = null;//Primera vez que se  ejecuta la refencia a null hasta que no elija archivo
		
		//Filtro de extensiones para cuando creemos el boton de elejir archivo
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de Musica (.mp3, .wav .aac .pcm)", "mp3", "wav", "aac", "pcm");
		
        
        //Ventana principal del programa
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 588, 338);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		//Titulo label del programa
		tituloReproductorJ = new JLabel("Reproductor musica Java");
		tituloReproductorJ.setToolTipText("Programado por Pablo Avila pablo98ad");
		tituloReproductorJ.setFocusable(false);
		tituloReproductorJ.setHorizontalAlignment(SwingConstants.CENTER);
		tituloReproductorJ.setForeground(Color.ORANGE);
		tituloReproductorJ.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tituloReproductorJ.setBounds(10, 11, 562, 22);
		frame.getContentPane().add(tituloReproductorJ);
		
		borrarArchivos = new JButton("x");
		borrarArchivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//al pulsar la x RESETEAMOS TODO
				mus.pausa();
				mus=null;
				histMus= new ArrayList<HistoricoSonido>();
				indiceArchivoActual=-1;
				actualizarHistorico();
				resultado.setText(primerVezTitulo("Esperando archivo..."));
        		tiempo.setText("");
        		sliderProgreso.setValue(0);
        		frame.requestFocusInWindow();//para que se pueda usar el teclado
			}
		});
		borrarArchivos.setForeground(Color.RED);
		borrarArchivos.setBackground(Color.GRAY);
		borrarArchivos.setMargin(new Insets(0, 0, 0, 0));
		borrarArchivos.setBounds(50, 109, 20, 16);
		frame.getContentPane().add(borrarArchivos);
		
		//titulo del archivo que se va moviendo con una marquesina hacia la derecha
		resultado = new JTextField(primerVezTitulo("Esperando archivo..."));//al principio si no escoje archivo
		resultado.setBackground(new Color(102, 0, 255));
		resultado.setForeground(Color.MAGENTA);
		resultado.setFont(new Font("Tahoma", Font.BOLD, 13));
		resultado.setHorizontalAlignment(SwingConstants.LEFT);
		resultado.setEditable(false);
		resultado.setBounds(10, 44, 562, 22);
		frame.getContentPane().add(resultado);
		resultado.setColumns(10);
		
		//BOTON REANUDAR
		reanudar.setFont(new Font("Tahoma", Font.PLAIN, 10));
		reanudar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mus!=null) {//si no hay archivo no se ejecuta
					//se esconde este boton y sale el de pausa
					pausa.setVisible(true);
					reanudar.setVisible(false);
					mus.reanudar();//reanudamos la cancion cuando pulsamos en reanudar
				}
				frame.requestFocusInWindow();//para que se pueda usar el teclado
			}
		});
		reanudar.setBounds(249, 268, 60, 30);
		frame.getContentPane().add(reanudar);
		reanudar.setVisible(false);
		
		
		
		//BOTON PAUSA
		pausa.setFont(new Font("Tahoma", Font.PLAIN, 10));
		pausa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mus!=null) {//si no hay archivo no se ejecuta
					//se esconde este boton y sale el de reanudar
					reanudar.setVisible(true);
					pausa.setVisible(false);
					mus.pausa();//pausamos la cancion cuando pulsamos en el boton pausa
				}
				frame.requestFocusInWindow();//para que se pueda usar el teclado
				
			}
		});
		pausa.setBounds(249, 268, 60, 30);
		frame.getContentPane().add(pausa);

		
		//Cuadro de texto donde vemos los segundos de la cancion
		tiempo = new JTextField();
		tiempo.setFocusable(false);
		tiempo.setHorizontalAlignment(SwingConstants.CENTER);
		tiempo.setBounds(30, 268, 60, 30);
		frame.getContentPane().add(tiempo);
		tiempo.setColumns(10);
		
		
		//SLIDER AJUSTAR VOLUMEN
		sliderVol = new JSlider();
		sliderVol.setToolTipText("Ajusta el volumen de reproducci\u00F3n");
		sliderVol.setOrientation(SwingConstants.VERTICAL);
		//lisenner para si movemos la ruleta cuando el raton entre por el slider ajustar el volumen
		sliderVol.addMouseWheelListener(new MouseWheelListener() {
		    @Override
		    public void mouseWheelMoved(MouseWheelEvent e) {
		        int direccion = e.getWheelRotation();
		        if (direccion < 0) {
		            //direccion arriba
		            sliderVol.setValue(sliderVol.getValue() + 5);
		        } else {
		        	//direccion abajo
		            sliderVol.setValue(sliderVol.getValue() - 5);
		        }
		    }
		});
		//lissener para que cuando clikeemos en el slider y le cambiemos el valor se cambie el volumen
		sliderVol.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(mus!=null) {
				mus.ajustarVolumen(sliderVol.getValue());
				}
				frame.requestFocusInWindow();//para que se pueda usar el teclado
			}
		});
		sliderVol.setBounds(548, 76, 24, 162);
		frame.getContentPane().add(sliderVol);
		
		
		
		//si la cancion se esta reproduciendo
		//PARA CADA CAMBIO  bps DE LA CANCION
			/*
		mus.getMediaPlayer().currentTimeProperty().addListener(new javafx.beans.value.ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
            	if(mus.getMediaPlayer().getStatus()==javafx.scene.media.MediaPlayer.Status.PLAYING || mus.getMediaPlayer().getStatus()==javafx.scene.media.MediaPlayer.Status.READY && mus.getMediaPlayer()!=null) {
	            	sliderProgreso.setValue(mus.obtenerProgreso());
	            	//sliderProgreso.setValue(sliderProgreso.getValue());
	            	tiempo.setText(mus.getProgreso());
	            	try {
	            		mus.getMediaPlayer().play();
	            	}catch(Exception e) {}
	            	System.out.println(sliderProgreso.getValue());
	            	System.out.println(mus.obtenerProgreso());
	            	System.out.println(mus.getProgreso());
	            	System.out.println();
            	}else {
            		System.out.println("sdfsdfsdf");
            	}
            }
		}
            );
		*/
		
		//SLIDER PROGRESO ARCHIVO
		sliderProgreso = new JSlider();
		sliderProgreso.setToolTipText("Ajusta la reproduccion arrastrando la flecha o con la ruleta del raton");
		sliderProgreso.setValue(0);
		sliderProgreso.setMaximum(1000);
		//Para que cuando se arrastre el raton sobre el slider ajuste el progreso de la cancion
				sliderProgreso.addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseDragged(MouseEvent arg0) {
						if(mus!=null) {
							mus.ajustarTiempo(sliderProgreso.getValue());
						}
						frame.requestFocusInWindow();//para que se pueda usar el teclado
					}
				});
				//lissener para que cuando movamos la ruleta estando en el slider se mueva la cancion
				sliderProgreso.addMouseWheelListener(new MouseWheelListener() {
				    @Override
				    public void mouseWheelMoved(MouseWheelEvent e) {
				    	if(mus!=null) {
					        int direccion = e.getWheelRotation();
					        if (direccion > 0) {
					            //direccion derecha
					        	sliderProgreso.setValue(sliderProgreso.getValue() + 10);
					        } else {
					        	//direccion izquiierda
					        	sliderProgreso.setValue(sliderProgreso.getValue() - 10);
					        }
					        mus.ajustarTiempo(sliderProgreso.getValue());
				    	}
				    }
				});
		sliderProgreso.setBounds(20, 242, 518, 22);
		frame.getContentPane().add(sliderProgreso);
		
		
		//LISTA DE CANCIONES HISTORICAS
		historico = new JList() ;
		historico.setToolTipText("<html><p>Pulsa click izquierdo para reproducir</p><p>Click derecho para eliminar</p></html>");
		JScrollPane desliz= new JScrollPane(historico);
		desliz.setBounds(49, 128, 472, 108);
		frame.getContentPane().add(desliz);
		
		
		//SELECTOR DE VELOCIDAD
				selectorVelocidad = new JComboBox();
				selectorVelocidad.setToolTipText("Cambia la velocidad de reproduccion");
				selectorVelocidad.setMaximumRowCount(9);
				selectorVelocidad.setAutoscrolls(true);
				selectorVelocidad.setBounds(461, 268, 60, 30);
				frame.getContentPane().add(selectorVelocidad);
				selectorVelocidad.addItem("x0.25");
				selectorVelocidad.addItem("x0.5");
				selectorVelocidad.addItem("x0.75");
				selectorVelocidad.addItem("x0.90");
				selectorVelocidad.addItem("x1");
				selectorVelocidad.addItem("x1.10");
				selectorVelocidad.addItem("x1.25");
				selectorVelocidad.addItem("x1.5");
				selectorVelocidad.addItem("x2");
				selectorVelocidad.addItem("x4");
				selectorVelocidad.setSelectedIndex(4);
				//selectorVelocidad.set
				selectorVelocidad.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(mus!=null) {
							mus.ajustarVelocidad(Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())));
						}
						frame.requestFocusInWindow();//para que se pueda usar el teclado
					}
				});
				selectorVelocidad.addMouseWheelListener(new MouseWheelListener() {
					public void mouseWheelMoved(MouseWheelEvent e) {
						if(mus!=null) {
					        int direccion = e.getWheelRotation();
					        if (direccion > 0) {
					            //direccion abajo
					        	if(selectorVelocidad.getSelectedIndex()!=0) {
					        		selectorVelocidad.setSelectedIndex(selectorVelocidad.getSelectedIndex()-1);
					        	}
					        } else {
					        	//direccion arriba
					        	if(selectorVelocidad.getSelectedIndex()!=selectorVelocidad.getItemCount()-1/*el numero total de items del jcombobox*/) {
					        		selectorVelocidad.setSelectedIndex(selectorVelocidad.getSelectedIndex()+1);
					        	}
					        }
					        mus.ajustarVelocidad(Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())));
					    	}
						frame.requestFocusInWindow();
					}
				});
				
		
		
		
		//BOTON ELEGIR CANCION
		eligeFileBoton = new JButton("Elegir Canciones...");
		eligeFileBoton.setToolTipText("Elije un archivo de audio");
		//le cambiamos el primer directorio a buscar por el escritorio del usuario que esta
		chooser.setCurrentDirectory(new File  
				(System.getProperty("user.home") + System.getProperty("file.separator")+ "Desktop"));
        
		eligeFileBoton.setMargin(new Insets(0, 0, 0, 0));
		eligeFileBoton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				chooser.setFileFilter(filter);//le asignamos el filtro de archivos al elegidor
				chooser.setMultiSelectionEnabled(true);//le decimos que podemos seleccionar mas de un archivo
		        chooser.setDialogTitle("Elige uno o varios archivos de audio");
				int returnVal = chooser.showOpenDialog(null);
		        File[] files = chooser.getSelectedFiles();
		        //SE PUEDEN AÑADIR MAS DE 1 FILE
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		        	for(int i=0; i<files.length;i++) {
		        		histMus.add(new HistoricoSonido(files[i].getAbsolutePath(),getNombreCancion(files[i].getAbsolutePath())));
		        		
		        	}
		        	comprobarSePuedenReproducir();//comprobamos que esten bien los ficheros
		        	//si ya habia archivo elegido
		        	if(mus!=null)  {
		        		/*mus.getMediaPlayer().stop();//paramos la cancion
		        		mus.cambiarCancion(files[0].getAbsolutePath(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
		        		HistoricoSonido d= new HistoricoSonido(mus.getPath(), mus.getNombreCancion());
			        	//histMus.add(d);
		        		indiceArchivoActual=histMus.size()-1;*/
		        	
		        	}else {//si es la primera vez que se elije un archivo
		        		mus = new ReproductorMusica(histMus.get(0).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
		        		HistoricoSonido d= new HistoricoSonido(mus.getPath(), mus.getNombreCancion());
			        	//histMus.add(d);
		        		indiceArchivoActual=0;
		        		mus.reproducirInicio();//lo reproducimos
		        		lisenerFinArchivo();
		        	}
		        	//añadimos esa ruta y nombre de archivo al historico
		        	
		        	
		        	
		        	
		        	//Crear un objeto DefaultListModel
		        	DefaultListModel listModel = new DefaultListModel();
		        	//Recorrer el contenido del ArrayList
		        	for(int i=0; i<histMus.size(); i++) {
		        	    //Añadir cada elemento del ArrayList en el modelo de la lista
		        	    listModel.add(i, histMus.get(i));
		        	}
		        	//Asociar el modelo de lista al JList
		        	historico.setModel(listModel);
		        	
		        	
		        	
		        	
		        	//System.out.println("lo añado a la posicion"+ indiceArchivoActual);
		        	//selectorVelocidad.setSelectedIndex(4);//para no desincronizarlo!
		            
		            resultado.setText(primerVezTitulo(mus.getNombreCancion()));//cambiamos el titulo 
		            
		            
		            
		        }else {
		        //	resultado.setText("Intentalo de nuevo");
		        }
		        frame.requestFocusInWindow();
		        
				
				
			}
		});
		eligeFileBoton.setBounds(234, 77, 110, 22);
		frame.getContentPane().add(eligeFileBoton);
		
		
		//BOTON REPETICION CANCION
		repeticionBoton = new JButton("🔂");
		repeticionBoton.setToolTipText("Pulsa para que se repita la cancion o no");
		colorBotonRepe= repeticionBoton.getBackground();//para poder volver al estilo inicial
		
		repeticionBoton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mus!=null && repetirOAleatorio.getSelectedIndex()!=0 && repetirOAleatorio.getSelectedIndex()!=1) {
					if(mus.getSeVaRepetir()==false) {
						mus.setRepeticion(true);
						repeticion=true;
						repeticionBoton.setText("\uD83D\uDD04");
						repeticionBoton.setToolTipText("Reproducir siempre");
						repeticionBoton.setBackground(new Color(102,255,51));
					}else {
						mus.setRepeticion(false);
						repeticion=false;
						repeticionBoton.setText("🔂");
						repeticionBoton.setToolTipText("Reproducir una vez");
						repeticionBoton.setBackground(colorBotonRepe);//para volver al estilo inicial
					}
				}
				frame.requestFocusInWindow();
				
			}
		});
		repeticionBoton.setMargin(new Insets(0, 0, 0, 0));
		repeticionBoton.setBounds(391, 268, 60, 30);
		frame.getContentPane().add(repeticionBoton);
		
		txtHistorico = new JTextField("Archivos Seleccionados");
		txtHistorico.setFocusable(false);
		txtHistorico.setMargin(new Insets(0, 0, 0, 0));
		txtHistorico.setHorizontalAlignment(SwingConstants.CENTER);
		txtHistorico.setForeground(new Color(250, 128, 114));
		txtHistorico.setFont(new Font("Tahoma", Font.BOLD, 13));
		txtHistorico.setEditable(false);
		txtHistorico.setColumns(10);
		txtHistorico.setBackground(Color.GRAY);
		txtHistorico.setBounds(49, 108, 472, 18);
		frame.getContentPane().add(txtHistorico);
		
		//BOTON ATRAS ARCHIVO
		botonAtras = new JButton("<<");
		botonAtras.setToolTipText("Pulsa tambien tecla izquierda");
		botonAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mus!=null && histMus.size()!=1) {
					System.out.println(indiceArchivoActual);
					
					if(indiceArchivoActual!=0) {
						mus.cambiarCancion(histMus.get(indiceArchivoActual-1).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
			            mus.reproducirInicio();
			            resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
			            indiceArchivoActual--;
			            historico.setSelectedIndex(indiceArchivoActual);
			            lisenerFinArchivo();
					}else {
						mus.cambiarCancion(histMus.get(histMus.size()-1).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
			            mus.reproducirInicio();
			            resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
			            indiceArchivoActual=histMus.size()-1;
			            historico.setSelectedIndex(indiceArchivoActual);
			            lisenerFinArchivo();
					}
				
				}
				
			}
		});
		botonAtras.setBounds(174, 268, 60, 30);
		frame.getContentPane().add(botonAtras);
		
		
		//BOTON ADELANTE ARCHIVO
		botonAdelante = new JButton(">>");
		botonAdelante.setToolTipText("Pulsa tambien tecla derecha");
		botonAdelante.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mus!=null && histMus.size()!=1) {
					//comprobarSePuedenReproducir();
					System.out.println(indiceArchivoActual);

					if(repetirOAleatorio.getSelectedIndex()!=0) {

						if(indiceArchivoActual!=histMus.size()-1) {
							if(mus==null) {
								mus= new ReproductorMusica(histMus.get(indiceArchivoActual+1).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);

							}else {
								mus.cambiarCancion(histMus.get(indiceArchivoActual+1).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);

							}
							mus.reproducirInicio();
							resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
							indiceArchivoActual++;
							historico.setSelectedIndex(indiceArchivoActual);
							lisenerFinArchivo();

						}else {//si se da al siguiente 
							mus.cambiarCancion(histMus.get(0).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
							mus.reproducirInicio();
							resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
							indiceArchivoActual=0;
							historico.setSelectedIndex(indiceArchivoActual);
							lisenerFinArchivo();
						}

					}else {//si esta puesto aleatorio reproducimos una aleatoria
						int fileAleatorio= (int)(Math.random()*histMus.size());
						mus.cambiarCancion(histMus.get(fileAleatorio).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
						mus.reproducirInicio();
						resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
						indiceArchivoActual=fileAleatorio;
						historico.setSelectedIndex(indiceArchivoActual);
						lisenerFinArchivo();

					}

				}

			}
		});
		botonAdelante.setBounds(321, 268, 60, 30);
		frame.getContentPane().add(botonAdelante);
		
		
		//Selecctor que decide que pasa al terminar la reproduccion del archivo actual
		repetirOAleatorio = new JComboBox(new String[] { "     \ud83d\udd00", "     ⏬", "     \u25BA"});
		repetirOAleatorio.setToolTipText("Cambia que pasa al terminar la reproduccion");
		repetirOAleatorio.setFont(UIManager.getFont("Button.font"));
		repetirOAleatorio.setSelectedIndex(1);
		repetirOAleatorio.setBounds(100, 268, 60, 30);
		frame.getContentPane().add(repetirOAleatorio);
		repetirOAleatorio.setRenderer(new BasicComboBoxRenderer() {
	         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        	 String[] mensajes= new String[] { "Reproducir aleatoriamente los siguientes archivos", "Reproducir por su orden los siguientes archivos", "Ninguna cancion nueva al finalizar"};
	        	 
	        	 if (isSelected) {
	               setBackground(list.getSelectionBackground());
	               setForeground(list.getSelectionForeground());
	               if (index > -1) {
	                  list.setToolTipText(mensajes[index]);
	               }
	            }
	            else {
	               setBackground(list.getBackground());
	               setForeground(list.getForeground());
	            }
	            setFont(list.getFont());
	            setText((value == null) ? "" : value.toString());
	  
	            return this;
	         }
	      });
		repetirOAleatorio.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        if((repetirOAleatorio.getSelectedIndex()==0 || repetirOAleatorio.getSelectedIndex()==1) && repeticionBoton.getToolTipText().equalsIgnoreCase("Reproducir siempre") ) {
		        	mus.setRepeticion(false);
					repeticion=false;
					repeticionBoton.setText("🔂");
					repeticionBoton.setToolTipText("Reproducir una vez");
					repeticionBoton.setBackground(colorBotonRepe);//para volver al estilo inicial
		        }
		        frame.requestFocusInWindow();//para que se pueda usar el teclado
		    }
		});
		
		
		//BOTON OBTENER ARCHIVOS SONIDO SISTEMA
		JButton botonObtenerSonido = new JButton("<html><p>Obtener</p><p> sonidos</p><p> sistema...</p></html>");
		botonObtenerSonido.setFont(new Font("Tahoma", Font.BOLD, 8));
		botonObtenerSonido.setToolTipText("<html><p>Puede tardar bastante; busca archivos</p><p> de audio en el disco de donde se</p><p>esta ejecutando este programa</p></html>");
		botonObtenerSonido.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Le preguntamos al usuario y le informamos que va a tardar
				int confirmed = JOptionPane.showConfirmDialog(null, "Esta operacion puede tardar bastantes minutos ¿Quiere Continuar?","Confirmacion que va a tardar bastante",JOptionPane.YES_NO_OPTION);
		        if(confirmed == JOptionPane.YES_OPTION){//si dice que si pues vamos
		        	obtenerTodosFicherosMusicaSistema();
					comprobarSePuedenReproducir();
					lisenerFinArchivo();
					frame.requestFocusInWindow();//para que se pueda usar el teclado
		        }
				
			}
		});
		botonObtenerSonido.setActionCommand("");
		botonObtenerSonido.setMargin(new Insets(0, 0, 0, 0));
		botonObtenerSonido.setBounds(480, 74, 40, 30);
		frame.getContentPane().add(botonObtenerSonido);
		
		
		
		
		
		
		
		
		
		Timer tiempoYBarraProgreso = new Timer (20, new ActionListener () //hacemos un hilo para que se actualize la duracion de la musica
				{ 
			public void actionPerformed(ActionEvent e) 
			{ 
				try {
				//System.out.println(mus.getMediaPlayer().getStatus().);
				if(mus!=null && mus.getMediaPlayer()!=null) {
					if(mus.getMediaPlayer().getStatus()==javafx.scene.media.MediaPlayer.Status.PLAYING || mus.getMediaPlayer().getStatus()==javafx.scene.media.MediaPlayer.Status.READY && mus.getMediaPlayer()!=null) {
						sliderProgreso.setValue(mus.obtenerProgreso());
						//	System.out.println(mus.obtenerProgreso()+"   "+sliderProgreso.getValue());
						//sliderProgreso.setValue(sliderProgreso.getValue());
						tiempo.setText(mus.getProgreso());

						mus.getMediaPlayer().play();
					}
				}
			}catch(Exception e1) {}

			} 
				}); 
		tiempoYBarraProgreso.start();
		
		//EFECTO MARQUESINA DEL TITULOO DEL ARCHIVO, VARIABLE RESULTADO
		Timer efectoMarquesinaTituloCancion = new Timer (200, new ActionListener () //hacemos un hilo para que se actualize la duracion de la musica
				{ 
				    public void actionPerformed(ActionEvent e) 
				    { 
				    	String resultadoo=resultado.getText();
				    	String p= resultadoo.substring(resultadoo.length()-1,resultadoo.length())+resultadoo.substring(0,resultadoo.length()-1);
				    	resultado.setText(p);
				    }
				}); 
		efectoMarquesinaTituloCancion.start();
		
		
		
		//COMPROBACION DE DISPONIBILIDAD y duplicados DE ARCHIVOS
				Timer comprobarRutasFiles = new Timer (10000, new ActionListener () //hacemos un hilo para que se actualize la duracion de la musica
						{ 
						    public void actionPerformed(ActionEvent e) 
						    { 
						    	comprobarDisponibilidadYDuplicadosFiles();
						    	
						    }
						}); 
				comprobarRutasFiles.start();
				
				/*
				//DESHABILITADO POR CONSUMIR MUCHOS RECURSOS
				
				Timer comprobarSePuedenReproducir = new Timer (5000, new ActionListener () //hacemos un hilo para que se actualize la duracion de la musica
						{ 
					public void actionPerformed(ActionEvent e) 
					{ 

						//COMPROBAMOS SI LOS ARCHIVOS SE PUEDEN REPRODUCIR
						Media testM = null;
						for(int i=0; i<histMus.size();i++) {
							try {
								
								testM= new Media(histMus.get(i).getDireccion());
								testM.getError();
								testM=null;
								
							}catch(MediaException  e3) {

								if (e3.getType() == MediaException.Type.MEDIA_UNSUPPORTED) {
									histMus.remove(i); 
									System.out.println("Borrado "+i);
								}
							}
						}
					}
						}); 
				comprobarSePuedenReproducir.start();
				
				*/
				
				
		
		//CONTROLADOR RATON SELECCION HISTORICO DE ARCHIVOS
		historico.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        JList list = (JList)evt.getSource();
		        if (SwingUtilities.isLeftMouseButton(evt)/*evt.getClickCount() == 1*/) {
		            
		        	int index = list.locationToIndex(evt.getPoint());
		            System.out.println("Pulsado indice: "+index);
		            if(mus==null) {
		            	mus= new ReproductorMusica(histMus.get(index).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
				           
		            }else {
		            	 mus.cambiarCancion(histMus.get(index).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
				           
		            	
		            }
		            mus.reproducirInicio();
		            if(reanudar.isVisible()) {//si esta la cancion actual pausada, le ponemos el boton bien
		            	reanudar.setVisible(false);
		            	pausa.setVisible(true);
		            	
		            }
		            
		            resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
		            indiceArchivoActual=index;
		            lisenerFinArchivo();
		            
		        }
		        if (SwingUtilities.isRightMouseButton(evt)/*evt.getClickCount() == 3*/) {//Si pulsa 3 veces se elimina
		        	int index = list.locationToIndex(evt.getPoint());
		    		System.out.println("Pulsado derecho");
		    		HistoricoSonido borrado=histMus.remove(index);
		    		
		        	//borramos el elemento que pulsamos doble click y actualizamos el cuadro
		    		
		    		actualizarHistorico();
		    		//Eso es igual a esto
		    		
		    		/*DefaultListModel listModel = new DefaultListModel();
		        	//Recorrer el contenido del ArrayList
		        	for(int i=0; i<histMus.size(); i++) {
		        	    //Añadir cada elemento del ArrayList en el modelo de la lista
		        	    listModel.add(i, histMus.get(i));
		        	}
		        	//Asociar el modelo de lista al JList
		        	historico.setModel(listModel);*/
		        	
		        	
		        	if(histMus.size()==0) {//si no hay nada en el historial se deja de reproducir el que esta
		        		indiceArchivoActual=-1;
		        		mus.pausa();
		        		mus=null;
		        		resultado.setText(primerVezTitulo("Esperando archivo..."));
		        		tiempo.setText("");
		        		sliderProgreso.setValue(0);
		        		indiceArchivoActual=-1;
		        	}else if(mus.getPath().equalsIgnoreCase(borrado.getDireccion())) {
		        			int cancionAReproducir;
		        			cancionAReproducir=histMus.size()-1;
		        			indiceArchivoActual=cancionAReproducir;
		        			mus.cambiarCancion(histMus.get(cancionAReproducir).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
					        mus.reproducirInicio();
					        resultado.setText(primerVezTitulo(mus.getNombreCancion()));
					        lisenerFinArchivo();
		        	}

		        	
		        	
		    		
		        	//lisenerFinArchivo();
		    		
		    		
		        }
		        frame.requestFocusInWindow();

		            // Triple-click detected
		            //int index = list.locationToIndex(evt.getPoint());
		            
		    }
		});
		
		
		frame.setFocusable(true);//para que nada mas abramos la aplicacion podamos pulsar teclas
		//eventos teclas
        frame.addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){
                
            }
            public void keyPressed(KeyEvent e){
            	//System.out.println(e.getKeyCode());
                if(e.getKeyCode()==e.VK_SPACE){
                	if(mus!=null) {
                		if(reanudar.isVisible()) {
                			reanudar.doClick();
                		}else {
                			pausa.doClick();
                		}
                		
                	}
                   // btnElegir.doClick();//como si se estubiera pulsando el boton con un click
                   
                }
                if(e.getKeyCode()==37){
                    botonAtras.doClick();
                }
                if(e.getKeyCode()==39){
                    botonAdelante.doClick();
                }
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                	//llamamos al la fuincion de  cerra para que la captura el lisenner de abajo
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            }
            public void keyReleased(KeyEvent e){
                
            }
        });
		
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Saliendo");	
            	guardarHistorico();//guardamos los archivos de audio 
                   System.exit(0);//salimos
             }
            });
        
        
        if(existeArchivoHisto()) {//si ya abrio el programa
			//System.out.println("Existe");
			histMus=obtenerHistoricoMusica();
			if(histMus.size()!=0 && histMus!=null) {
				actualizarHistorico();
				mus= new ReproductorMusica(histMus.get(0).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
				//mus.cambiarCancion(histMus.get(0).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
		        mus.reproducirInicio();
		        indiceArchivoActual=0;
		        resultado.setText(primerVezTitulo(mus.getNombreCancion()));
		        
		        
		        lisenerFinArchivo();
		        
				
			}
			
			
		}else {//si es la primera vez que abre el programa
			crearArchivosApp();
			histMus = new ArrayList<HistoricoSonido>();
		}
		
		frame.setTitle("Reproductor Musica Java v1.81 by Pablo98ad");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconoRMJ.png")));
		
	}
	/**
	 * Metodo que guarda el historial de archivos
	 */
	public  void guardarHistorico() {
		String linea;
		BufferedWriter fw=null;
		String direccion=System.getenv("APPDATA")+"\\pablo98ad\\reproductorSonidoJava\\historial.json";
		JSONObject archivo;
		try {
			fw=new BufferedWriter(new FileWriter(direccion,false));//abrimos el flujo de escritura diciendole que no sobreescriba lo que hay
		
			for (int i=0; i<histMus.size();i++) {
				try {
					archivo = new JSONObject(histMus.get(i));
					linea=archivo.toString();
					fw.write(linea);//escribimos la linea en el fichero
					fw.newLine();//escribimos un salto de linea

				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.printf("Error de Entrada/Salida: %s \n",e.getMessage());
		}finally {
			if (fw!=null) {
				try{
					fw.close();//cerramos el flujo de escritura
				} catch (IOException e) {
					System.out.printf("Error de Entrada/Salida: %s \n",e.getMessage());
				}
				}
			}		
	}
	
	
	
	public void comprobarDisponibilidadYDuplicadosFiles() {
		File f;
		//COMPROBAR QUE EXISTEN
		for(int i=0; i<histMus.size();i++) {
			f= new File(histMus.get(i).getDireccion());
			if(!f.exists()) {
				histMus.remove(i);
			}
		}
		
		//ELIMINAR DUPLICADOS (ARCHIVOS CON LA MISMA RUTA)
		 for(int i=histMus.size()-1; i>0; i--) {
		        for(int j=i-1; j>=0; j--) {
		            if(histMus.get(i).equals(histMus.get(j))) {
		            	histMus.remove(i);
		                break;
		            }
		        }
		    }
		 
		actualizarHistorico();
		historico.setSelectedIndex(indiceArchivoActual);//para que no se deseleccione la cancion reproducida
	}
		
	
	private String primerVezTitulo(String t) {
		//al cambiar una cancion o el primer mensaje que aparece en el texto resultado es tratado por este metodo
		//para poder hacer la marquesina en direccion derecha
		String espacios="                                                                                                                            ";
		t="\u200e"+t+"\u200f";
		String pantalla= espacios.substring(0,(espacios.length()/2)-t.length())+t;
		pantalla=pantalla+espacios.substring(0, espacios.length()-pantalla.length());

		return pantalla;
	}
	
	
	public void comprobarSePuedenReproducir() {
		//COMPROBAMOS SI LOS ARCHIVOS SE PUEDEN REPRODUCIR
		Media testM = null;
		//MediaPlayer testMP=null;
		for(int i=0; i<histMus.size();i++) {
			try {
				
				testM= new Media(new File(histMus.get(i).getDireccion()).toURI().toString());
				testM.getError();
				if(!canPlayContentType(filenameToContentType(histMus.get(i).getDireccion()))) {
					System.out.println("Borrado "+i);
					histMus.remove(i); 
				}else {
					System.out.println("Se puede reproducir "+i);
				}
				
				
				
				//testMP=  new MediaPlayer(testM);
				//testMP.play();
				//testMP.getError();
				//testMP.setVolume(0);
				//Thread.sleep(100);
				//testMP.stop();
				//System.out.println("Comprobado "+i);
			}catch(MediaException  e3) {
				//if (e3.getType() == MediaException.Type.MEDIA_UNSUPPORTED) {
					histMus.remove(i); 
					System.out.println("Borrado "+i);
				//}
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		actualizarHistorico();
		
	}
	
	
	public String getNombreCancion(String ruta) {
		String n= ruta.substring(ruta.lastIndexOf('\\')+1, ruta.lastIndexOf('.'));
		//por ejempo "C:/Pablo/Escritorio/hola.mp3" cojeria solo "hola"
		return n;
	}
	
	
	
	
	
	public ArrayList<HistoricoSonido> obtenerHistoricoMusica(){
		ArrayList<HistoricoSonido> h= new ArrayList<HistoricoSonido>();
		BufferedReader fr=null;
		String linea;
		String direccion=System.getenv("APPDATA")+"\\pablo98ad\\reproductorSonidoJava\\historial.json";
		JSONObject archivo;
		
		try {
			fr=new BufferedReader(new FileReader(direccion));//abrimos el flujo de escritura diciendole que no sobreescriba lo que hay
			
			linea= fr.readLine();//leemos la primera linea		
			while(linea!=null) {//si no hay linea nos salimos del bucle
				try {
					archivo = new JSONObject(linea);	
					h.add(new HistoricoSonido(archivo.getString("direccion"), archivo.getString("nombre")));
					//System.out.println(linea);
							
				}catch(JSONException e) {//si una linea esta mal escrita y da error no se interrumpe la carga
					e.printStackTrace();
				}
				linea= fr.readLine();
			}
		} catch (IOException e) {
			System.out.printf("Error de Entrada/Salida: %s \n",e.getMessage());
		}finally {
			if (fr!=null) {
				try{
					fr.close();//cerramos el flujo de escritura
				} catch (IOException e) {
					System.out.printf("Error de Entrada/Salida: %s \n",e.getMessage());
				}
				}
			}
		return h;
	}
	
	public void actualizarHistorico() {
		DefaultListModel listModel = new DefaultListModel();
    	//Recorrer el contenido del ArrayList
    	for(int i=0; i<histMus.size(); i++) {
    	    //Añadir cada elemento del ArrayList en el modelo de la lista
    	    listModel.add(i, histMus.get(i));
    	}
    	//Asociar el modelo de lista al JList
    	historico.setModel(listModel);
    	
    	
		
		
	}
	
	
	public void obtenerTodosFicherosMusicaSistema() {
		System.getenv("APPDATA");
		//Path ruta = 
		//String buscar= System.getProperty("user.home");
		//System.out.println(buscar);
		Path currentRelativePath = Paths.get("");
		String buscar = currentRelativePath.toAbsolutePath().toString().substring(0, 3);
		
		
		File f = new File(buscar);
        try {
            boolean recursivo = true;
            String[] ext= new String[] {"mp3","wav","aac", "pcm"};
            Collection files = FileUtils.listFiles(f, ext, recursivo);

            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                String fi = file.getName();
                    System.out.println(file.getAbsolutePath());
                    histMus.add(new HistoricoSonido(file.getAbsolutePath(), file.getName().substring(0,file.getName().length()-4)));
  
            }if(mus==null && histMus.size()!=0) {
            	mus= new ReproductorMusica(histMus.get(0).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
                mus.reproducirInicio();
                resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
                indiceArchivoActual=0;
                historico.setSelectedIndex(indiceArchivoActual);
            }
            
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		
		
		
	}
	
	
	public void lisenerFinArchivo() {
		//lisener si termina la reproduccion
        mus.getMediaPlayer().setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
            	//si selecciono que se va a reproducir la sigfuiente cancion
            	if(repetirOAleatorio.getSelectedIndex()==1) {
            		botonAdelante.doClick();
            		System.out.println("pongo siguiente solo");
            		//si selecciono que se va a reproducir aleatoriamente
            	}else if(repetirOAleatorio.getSelectedIndex()==0) {
            		int fileAleatorio= (int)(Math.random()*histMus.size());
            		mus.cambiarCancion(histMus.get(fileAleatorio).getDireccion(),sliderVol.getValue(), Double.parseDouble(selectorVelocidad.getSelectedItem().toString().substring(1, selectorVelocidad.getSelectedItem().toString().length())),repeticion);
		            mus.reproducirInicio();
		            resultado.setText(primerVezTitulo(mus.getNombreCancion())); 
		            indiceArchivoActual=fileAleatorio;
		            historico.setSelectedIndex(indiceArchivoActual);
            		
            		
            		
            	}
            	lisenerFinArchivo();//volvemos a esperar el fin de la cancion
            	
                
                System.out.println("fin");
            }
        });
		
	}
	
	public boolean existeArchivoHisto() {
		boolean existe=false;
		String direccion=System.getenv("APPDATA")+"\\pablo98ad\\reproductorSonidoJava\\historial.json";
		File f = new File(direccion);
		
		if(f.exists()) {
			existe=true;
		}
		return existe;
	}
	
	public void crearArchivosApp() {
		String direccion=System.getenv("APPDATA")+"\\pablo98ad\\reproductorSonidoJava\\historial.json";
		File f = new File(direccion);
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} catch (IOException e) {
			System.out.println("No se puede crear: "+e.getMessage());
		}
		
	}
	
	
	@Override
	public void start(Stage arg0) throws Exception {
		
		//mus.reproducirInicio();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					reproductorMusicaC window = new reproductorMusicaC();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}
