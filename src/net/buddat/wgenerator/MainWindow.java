package net.buddat.wgenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData.FlowerType;
import com.wurmonline.mesh.GrassData.GrowthStage;
import com.wurmonline.mesh.GrassData.GrowthTreeStage;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.util.Constants;
import net.buddat.wgenerator.util.ProgressHandler;
import net.buddat.wgenerator.util.StreamCapturer;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.Taskbar.State;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -407206109473532425L;

	private WurmAPI api;
	private HeightMap heightMap;
	private TileMap tileMap;
	private ArrayList<String> genHistory;
	private boolean apiClosed = true;
	private MapPanel mapPanel;
	private String mapName;
	private String actionsFileDirectory;
	private Constants.VIEW_TYPE defaultView = Constants.VIEW_TYPE.HEIGHT;
	private ProgressHandler progress;

	private Taskbar taskbar = Taskbar.getTaskbar();
	
	private JProgressBar progressBar;
	private JLabel lblMemory;
	private JPanel contentPane;
	private JTextField textField_mapSeed;
	private JTextField textField_mapResolution;
	private JTextField textField_mapMinEdge;
	private JTextField textField_mapBorderWeight;
	private JTextField textField_mapMaxHeight;
	private JTextField textField_mapIterations;
	private JTextField textField_erodeIterations;
	private JTextField textField_erodeMinSlope;
	private JTextField textField_erodeSediment;
	private JTextField textField_biomeSeed;
	private JTextField textField_dirtPerTile;
	private JTextField textField_maxDiagSlope;
	private JTextField textField_maxDirtHeight;
	private JTextField textField_waterHeight;
	private JTextField textField_seedCount;
	private JTextField textField_biomeSize;
	private JTextField textField_biomeMaxSlope;
	private JTextField textField_biomeMaxHeight;
	private JTextField textField_growthN;
	private JTextField textField_growthS;
	private JTextField textField_growthE;
	private JTextField textField_growthW;
	private JTextField textField_biomeMinHeight;
	private JTextField textField_Iron;
	private JTextField textField_Gold;
	private JTextField textField_Silver;
	private JTextField textField_Zinc;
	private JTextField textField_Copper;
	private JTextField textField_Lead;
	private JTextField textField_Tin;
	private JTextField textField_Marble;
	private JTextField textField_Slate;
	private JTextField textField_Addy;
	private JTextField textField_Glimmer;
	private JTextField textField_Rock;
	private JTextField textField_maxDirtSlope;
	private JTextField textField_mapName;
	private JComboBox<Integer> comboBox_mapSize;
	private JCheckBox checkbox_biomeRandomSeed;
	private JComboBox<Tile> comboBox_biomeType;
	private JCheckBox checkbox_moreLand;
	private JCheckBox checkbox_mapRandomSeed;
	private JButton btnGenerateHeightmap;
	private JButton btnErodeHeightmap;
	private JButton btnUpdateWater;
	private JButton btnDropDirt;
	private JButton btnGenerateOres;
	private JButton btnResetBiomes;
	private JButton btnUndoLastBiome;
	private JButton btnAddBiome;
	private JLabel lblWater;
	private JCheckBox chckbxAroundWater;
	private JTextField textField_growthMin;
	private JTextField textField_growthMax;
	private JCheckBox checkbox_growthRandom;
	private JButton btnSaveMapFiles;
	private JButton btnSaveImageDumps;
	private JButton btnSaveActions;
	private JButton btnLoadActions;
	private JButton btnViewHeight;
	private JButton btnViewCave;
	private JButton btnViewTopo;
	private JButton btnViewMap;
	private JTextField textField_cliffRatio;
	private JButton btnLoadHeightmap;
	private JTextField textField_erodeMaxSlope;
	private JCheckBox checkBox_landSlide;
	private JButton btnUpdateMapName;
	private JLabel lblMapCoords;
	private JCheckBox chcekbox_showGrid;
	private JTextField textField_mapGridSize;
	private JTextField textField_biomeDensity;
	private JButton btnViewErrors;
	private JTextArea textArea_Errors;
	private CardLayout cl_mainPanel;
	private JPanel mainPanel;
	private JCheckBox checkbox_paintMode;
	private JButton btnGenerateRivers;
	private JCheckBox checkbox_paintRivers;
	private JTextField textField_riverDepth;
	private JTextField textField_riverWidth;
	private JTextField textField_riverSlope;
	private JButton btnResetRivers;
	private JCheckBox checkbox_autoDropDirt;
	private JTextField textField_normalizeRatio;
	private JButton btnUndoRiver;

	private static String[][] biomeOptionValue = {       
			//Count,Size,MaxSlope,RateN,RateS,RateE,RateW,MinHeight,Maxheight,GrowtRandom,GrowMin,GrowMax,AroundWater,Density  
			{"500","5","20","70","70","70","70","20","20","true","30","70","true","1"},  //TILE_CLAY
			{"100","3","40","70","70","70","70","50","4000","true","30","70","true","2"},  //TILE_DIRT
			{"10","2","10","70","70","70","70","50","1000","true","30","70","true","1"},  //TILE_DIRT_PACKED
			{"100","10","40","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_GRASS
			{"10","2","30","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_GRAVEL
			{"100","10","20","70","70","70","70","30","0","true","30","70","true","2"},  //TILE_KELP
			{"5","1","140","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_LAVA
			{"100","5","20","70","70","70","70","30","2","true","30","70","true","1"},  //TILE_MARSH
			{"100","3","20","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_MOSS
			{"100","3","20","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_MYCELIUM
			{"50","5","20","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_PEAT
			{"100","10","20","70","70","70","70","30","0","true","30","70","true","2"},  //TILE_REED
			{"200","100","30","70","70","70","70","50","50","true","30","70","true","1"},  //TILE_SAND
			{"10","50","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_STEPPE
			{"200","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_TAR
			{"10","50","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_TUNDRA
			{"250","2","20","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_TREE_APPLE
			{"30","20","40","40","40","40","40","0","4000","true","30","70","true","1"},  //TILE_TREE_BIRCH
			{"30","20","20","40","40","40","40","0","4000","true","30","70","true","3"},  //TILE_TREE_CEDAR
			{"250","2","20","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_TREE_CHERRY
			{"5","100","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_TREE_CHESTNUT
			{"30","20","50","40","40","40","40","0","4000","true","30","70","true","1"},  //TILE_TREE_FIR
			{"250","2","20","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_TREE_LEMON
			{"30","20","20","40","40","40","40","0","4000","true","30","70","true","1"},  //TILE_TREE_LINDEN
			{"30","20","30","40","40","40","40","0","4000","true","30","70","true","1"},  //TILE_TREE_MAPLE
			{"250","1","20","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_TREE_OAK
			{"30","20","30","40","40","40","40","0","4000","true","30","70","true","2"},  //TILE_TREE_OLIVE
			{"30","20","50","40","40","40","40","0","4000","true","30","70","true","3"},  //TILE_TREE_PINE
			{"30","20","20","40","40","40","40","0","4000","true","30","70","true","1"},  //TILE_TREE_WALNUT
			{"250","1","20","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_TREE_WILLOW
			{"250","2","20","70","70","70","70","0","4000","true","30","70","true","2"},  //TILE_TREE_ORANGE
			{"500","1","40","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_CAMELLIA
			{"50","10","20","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_GRAPE
			{"500","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_LAVENDER
			{"500","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_OLEANDER
			{"500","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_ROSE
			{"500","1","50","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_THORN
			{"500","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_HAZELNET
			{"500","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_RASPBERRY
			{"500","1","50","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_BLUEBERRY
			{"500","1","50","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH_LINGONBERRY
			{"30","20","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_TREE
			{"500","1","30","70","70","70","70","0","4000","true","30","70","true","1"},  //TILE_BUSH
			{"10","20","50","70","70","70","70","0","4000","true","30","70","true","1"}}; //TILE_SNOW
	private JButton btnLoadBiomes;
	private JButton btnExportBiomes;
	private JButton btnImportBiomes;
	private JTextField textField_FlowerPercent;
	private JComboBox<String> comboBox_FlowerType;
	private JButton btnViewBiomes;
	private JTextField textField_Sandstone;
	private JTextField textField_Rocksalt;


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			defaults.put("nimbusOrange",new Color(50,205,50));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MainWindow() {
		setTitle(Constants.WINDOW_TITLE+" - v"+Constants.version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		
		taskbar.setWindowProgressState(this, State.NORMAL);
		
		JTabbedPane optionsPane = new JTabbedPane(JTabbedPane.TOP);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setString(Constants.READY);
		progressBar.setEnabled(true);
		progressBar.setValue(100);

		JPanel viewPanel = new JPanel();

		JPanel mapCoordsPanel = new JPanel();

		mainPanel = new JPanel();

		JPanel memoryPanel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
								.addComponent(mapCoordsPanel, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
								.addComponent(viewPanel, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(memoryPanel, GroupLayout.PREFERRED_SIZE, 315, GroupLayout.PREFERRED_SIZE)
								.addComponent(optionsPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap())
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(6)
						.addComponent(optionsPane, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(memoryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(mapCoordsPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(viewPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
		memoryPanel.setLayout(new GridLayout(0, 2, 5, 0));

		JLabel lblMemoryUsage = new JLabel("Memory Usage:");
		lblMemoryUsage.setHorizontalAlignment(SwingConstants.RIGHT);
		memoryPanel.add(lblMemoryUsage);

		lblMemory = new JLabel("xx% of xxgb");
		lblMemory.setFont(new Font("SansSerif", Font.PLAIN, 12));
		memoryPanel.add(lblMemory);
		lblMemory.setHorizontalAlignment(SwingConstants.CENTER);
		cl_mainPanel = new CardLayout(0,0);
		mainPanel.setLayout(cl_mainPanel);

		mapPanel = new MapPanel(this);
		mainPanel.add(mapPanel, "MAP");
		mapPanel.setGridSize(Constants.GRID_SIZE);

		JPanel errorPanel = new JPanel();
		mainPanel.add(errorPanel, "ERRORS");
		errorPanel.setLayout(new GridLayout(0, 1, 0, 0));

		textArea_Errors = new JTextArea();
		errorPanel.add(new JScrollPane(textArea_Errors));
		textArea_Errors.setEditable(false);

		JPanel panel_25 = new JPanel();

		JLabel lblNewLabel_4 = new JLabel("Map Coords:");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);

		lblMapCoords = new JLabel("");
		lblMapCoords.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel panel_28 = new JPanel();
		panel_28.setLayout(new GridLayout(0, 3, 0, 0));

		chcekbox_showGrid = new JCheckBox("Grid");
		panel_28.add(chcekbox_showGrid);
		chcekbox_showGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapPanel.showGrid(chcekbox_showGrid.isSelected());
			}
		});
		chcekbox_showGrid.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblSize = new JLabel("Size:");
		lblSize.setToolTipText("Grid cell count. Press enter to submit");
		lblSize.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_28.add(lblSize);

		textField_mapGridSize = new JTextField("" + Constants.GRID_SIZE);
		textField_mapGridSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					mapPanel.setGridSize(Integer.parseInt(textField_mapGridSize.getText()));
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null,"Map size must be an integer", "Input Error", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		panel_28.add(textField_mapGridSize);
		textField_mapGridSize.setColumns(10);
		GroupLayout gl_mapCoordsPanel = new GroupLayout(mapCoordsPanel);
		gl_mapCoordsPanel.setHorizontalGroup(
				gl_mapCoordsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_mapCoordsPanel.createSequentialGroup()
						.addComponent(panel_25, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(panel_28, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				);
		gl_mapCoordsPanel.setVerticalGroup(
				gl_mapCoordsPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(panel_25, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
				.addComponent(panel_28, GroupLayout.PREFERRED_SIZE, 24, Short.MAX_VALUE)
				);
		GroupLayout gl_panel_25 = new GroupLayout(panel_25);
		gl_panel_25.setHorizontalGroup(
				gl_panel_25.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_25.createSequentialGroup()
						.addComponent(lblNewLabel_4, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblMapCoords, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
						.addContainerGap())
				);
		gl_panel_25.setVerticalGroup(
				gl_panel_25.createParallelGroup(Alignment.LEADING)
				.addComponent(lblNewLabel_4, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
				.addComponent(lblMapCoords, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
				);
		panel_25.setLayout(gl_panel_25);
		mapCoordsPanel.setLayout(gl_mapCoordsPanel);

		btnViewMap = new JButton("View Map");
		viewPanel.add(btnViewMap);

		btnViewTopo = new JButton("View Topo");
		viewPanel.add(btnViewTopo);

		btnViewBiomes = new JButton("View Biomes");
		viewPanel.add(btnViewBiomes);

		btnViewCave = new JButton("View Cave");
		viewPanel.add(btnViewCave);

		btnViewHeight = new JButton("View Heightmap");
		viewPanel.add(btnViewHeight);

		btnViewErrors = new JButton("View Errors");
		btnViewErrors.setVisible(false);
		btnViewErrors.setBackground(new Color(255, 51, 51));
		btnViewErrors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cl_mainPanel.show(mainPanel,"ERRORS");
			}
		});
		viewPanel.add(btnViewErrors);

		JPanel heightmapPanel = new JPanel();
		optionsPane.addTab("Heightmap", null, heightmapPanel, null);
		optionsPane.setEnabledAt(0, true);

		JPanel panel_6 = new JPanel();

		JPanel panel = new JPanel();
		panel_6.add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel labelPanel = new JPanel();
		panel.add(labelPanel);
		labelPanel.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblMapSize = new JLabel("Map Size");
		labelPanel.add(lblMapSize);

		JLabel lblMapSeed = new JLabel("Map Seed");
		lblMapSeed.setToolTipText("Determines the randomness of the map");
		labelPanel.add(lblMapSeed);

		JLabel lblResolution = new JLabel("Resolution");
		lblResolution.setToolTipText("Less = islands, More = one landmass");
		labelPanel.add(lblResolution);

		JLabel lblItrerations = new JLabel("Iterations");
		labelPanel.add(lblItrerations);

		JLabel lblMinEdge = new JLabel("Min Edge");
		lblMinEdge.setToolTipText("Size of water border around map");
		labelPanel.add(lblMinEdge);

		JLabel lblBorderWeight = new JLabel("Border Weight");
		lblBorderWeight.setToolTipText("How spread out mountains are. Less = more centralized mountains");
		labelPanel.add(lblBorderWeight);

		JLabel lblMaxHeight = new JLabel("Max Height");
		labelPanel.add(lblMaxHeight);

		JLabel lblNormalizeRatio = new JLabel("Normalize Ratio");
		labelPanel.add(lblNormalizeRatio);

		JLabel label = new JLabel("");
		labelPanel.add(label);

		JLabel lblNewLabel = new JLabel("");
		labelPanel.add(lblNewLabel);

		JPanel inputPanel = new JPanel();
		panel.add(inputPanel);
		inputPanel.setLayout(new GridLayout(0, 1, 0, 2));

		comboBox_mapSize = new JComboBox<Integer>();
		inputPanel.add(comboBox_mapSize);
		comboBox_mapSize.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1024, 2048, 4096, 8192, 16384}));
		comboBox_mapSize.setSelectedIndex(1);

		textField_mapSeed = new JTextField("" + System.currentTimeMillis());
		textField_mapSeed.setEnabled(false);
		inputPanel.add(textField_mapSeed);
		textField_mapSeed.setColumns(10);

		textField_mapResolution = new JTextField("" + (int) Constants.RESOLUTION);
		inputPanel.add(textField_mapResolution);
		textField_mapResolution.setColumns(10);

		textField_mapIterations = new JTextField("" + Constants.HEIGHTMAP_ITERATIONS);
		inputPanel.add(textField_mapIterations);
		textField_mapIterations.setColumns(10);

		textField_mapMinEdge = new JTextField("" + Constants.MIN_EDGE);
		inputPanel.add(textField_mapMinEdge);
		textField_mapMinEdge.setColumns(10);

		textField_mapBorderWeight = new JTextField("" + Constants.BORDER_WEIGHT);
		inputPanel.add(textField_mapBorderWeight);
		textField_mapBorderWeight.setColumns(10);

		textField_mapMaxHeight = new JTextField("" + Constants.MAP_HEIGHT);
		inputPanel.add(textField_mapMaxHeight);
		textField_mapMaxHeight.setColumns(10);

		textField_normalizeRatio = new JTextField("" + Constants.NORMALIZE_RATIO);
		inputPanel.add(textField_normalizeRatio);
		textField_normalizeRatio.setColumns(10);

		checkbox_moreLand = new JCheckBox("More Land", Constants.MORE_LAND);
		inputPanel.add(checkbox_moreLand);

		checkbox_mapRandomSeed = new JCheckBox("Random Seed", true);
		inputPanel.add(checkbox_mapRandomSeed);
		checkbox_mapRandomSeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField_mapSeed.setEnabled(!checkbox_mapRandomSeed.isSelected());
			}
		});

		JPanel panel_7 = new JPanel();

		btnGenerateHeightmap = new JButton("Generate Heightmap");
		panel_7.add(btnGenerateHeightmap);
		GroupLayout gl_heightmapPanel = new GroupLayout(heightmapPanel);
		gl_heightmapPanel.setHorizontalGroup(
				gl_heightmapPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_heightmapPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_heightmapPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_6, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_heightmapPanel.setVerticalGroup(
				gl_heightmapPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_heightmapPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_6, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);

		btnLoadHeightmap = new JButton("Import Heightmap");
		btnLoadHeightmap.setToolTipText("16 bit grayscale PNG");
		panel_7.add(btnLoadHeightmap);
		heightmapPanel.setLayout(gl_heightmapPanel);

		JPanel erosionPanel = new JPanel();
		optionsPane.addTab("Erosion", null, erosionPanel, null);
		optionsPane.setEnabledAt(1, true);

		JPanel panel_1 = new JPanel();

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblIterations = new JLabel("Iterations");
		lblIterations.setToolTipText("How many times to pass over the map");
		panel_3.add(lblIterations);

		JLabel lblMinSlope = new JLabel("Min Slope");
		lblMinSlope.setToolTipText("Only erode above this slope");
		panel_3.add(lblMinSlope);

		JLabel lblMaxSlope_1 = new JLabel("Max Slope");
		lblMaxSlope_1.setToolTipText("Only erode below this slope");
		panel_3.add(lblMaxSlope_1);

		JLabel lblSedimentPer = new JLabel("Sediment per");
		lblSedimentPer.setToolTipText("How much dirt is dropped on each iteration");
		panel_3.add(lblSedimentPer);

		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4);
		panel_4.setLayout(new GridLayout(0, 1, 0, 2));

		textField_erodeIterations = new JTextField("" + Constants.EROSION_ITERATIONS);
		textField_erodeIterations.setColumns(10);
		panel_4.add(textField_erodeIterations);

		textField_erodeMinSlope = new JTextField("" + Constants.EROSION_MIN_SLOPE);
		textField_erodeMinSlope.setColumns(10);
		panel_4.add(textField_erodeMinSlope);

		textField_erodeMaxSlope = new JTextField("" + Constants.EROSION_MAX_SLOPE);
		panel_4.add(textField_erodeMaxSlope);
		textField_erodeMaxSlope.setColumns(10);

		textField_erodeSediment = new JTextField("" + Constants.EROSION_MAX_SEDIMENT);
		textField_erodeSediment.setColumns(10);
		panel_4.add(textField_erodeSediment);

		JPanel panel_5 = new JPanel();

		btnErodeHeightmap = new JButton("Erode Heightmap");
		panel_5.add(btnErodeHeightmap);
		GroupLayout gl_erosionPanel = new GroupLayout(erosionPanel);
		gl_erosionPanel.setHorizontalGroup(
				gl_erosionPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_erosionPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_erosionPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_erosionPanel.setVerticalGroup(
				gl_erosionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_erosionPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);
		erosionPanel.setLayout(gl_erosionPanel);

		JPanel dropDirtPanel = new JPanel();
		optionsPane.addTab("Dirt / Water", null, dropDirtPanel, null);
		optionsPane.setEnabledAt(2, true);

		JPanel panel_9 = new JPanel();

		JPanel panel_10 = new JPanel();
		panel_9.add(panel_10);
		panel_10.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_11 = new JPanel();
		panel_10.add(panel_11);
		panel_11.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblBiomeSeed = new JLabel("Biome Seed");
		panel_11.add(lblBiomeSeed);

		JLabel lblDirtPerTile = new JLabel("Dirt Per Tile");
		lblDirtPerTile.setToolTipText("How much dirt to drop per tile on the map");
		panel_11.add(lblDirtPerTile);

		JLabel lblNewLabel_1 = new JLabel("Max Dirt Slope");
		panel_11.add(lblNewLabel_1);

		JLabel lblMaxDirtSlope = new JLabel("Max Diagonal Slope");
		panel_11.add(lblMaxDirtSlope);

		JLabel lblMaxDirtHeight = new JLabel("Max Dirt Height");
		lblMaxDirtHeight.setToolTipText("Dirt is not dropped above this height");
		panel_11.add(lblMaxDirtHeight);

		JLabel lblWaterHeight = new JLabel("Water Height");
		lblWaterHeight.setToolTipText("Sea level");
		panel_11.add(lblWaterHeight);

		JLabel lblCliffRatio = new JLabel("Cliff Ratio");
		lblCliffRatio.setToolTipText("How much cliffs protrude. Less = buried cliffs, More = lots of cliffs");
		panel_11.add(lblCliffRatio);

		JLabel label_8 = new JLabel("");
		panel_11.add(label_8);

		JLabel label_4 = new JLabel("");
		panel_11.add(label_4);

		JSeparator separator = new JSeparator();
		panel_11.add(separator);

		checkbox_paintRivers = new JCheckBox("Paint Rivers");
		checkbox_paintRivers.setToolTipText("Click and drag on map to draw rivers");
		checkbox_paintRivers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapPanel.setRiverPaintingMode(checkbox_paintRivers.isSelected());
			}
		});
		panel_11.add(checkbox_paintRivers);

		checkbox_autoDropDirt = new JCheckBox("Auto Drop Dirt");
		checkbox_autoDropDirt.setToolTipText("Drop dirt after generating rivers");
		checkbox_autoDropDirt.setSelected(true);
		panel_11.add(checkbox_autoDropDirt);

		JLabel label_7 = new JLabel("");
		panel_11.add(label_7);

		JLabel lblRiverDepth = new JLabel("River depth");
		lblRiverDepth.setToolTipText("Deepest part of the river");
		panel_11.add(lblRiverDepth);

		JLabel lblRiverWidth = new JLabel("River width");
		lblRiverWidth.setToolTipText("Base size at the deepest part");
		panel_11.add(lblRiverWidth);

		JLabel lblRiverSlope = new JLabel("River slope");
		lblRiverSlope.setToolTipText("Lower = gradual, Higher = steep edges");
		panel_11.add(lblRiverSlope);

		JPanel panel_12 = new JPanel();
		panel_10.add(panel_12);
		panel_12.setLayout(new GridLayout(0, 1, 0, 2));

		textField_biomeSeed = new JTextField("" + System.currentTimeMillis());
		textField_biomeSeed.setEnabled(false);
		textField_biomeSeed.setColumns(10);
		panel_12.add(textField_biomeSeed);

		textField_dirtPerTile = new JTextField("" + Constants.DIRT_DROP_COUNT);
		textField_dirtPerTile.setColumns(10);
		panel_12.add(textField_dirtPerTile);

		textField_maxDirtSlope = new JTextField("" + Constants.MAX_DIRT_SLOPE);
		panel_12.add(textField_maxDirtSlope);
		textField_maxDirtSlope.setColumns(10);

		textField_maxDiagSlope = new JTextField("" + Constants.MAX_DIRT_DIAG_SLOPE);
		textField_maxDiagSlope.setColumns(10);
		panel_12.add(textField_maxDiagSlope);

		textField_maxDirtHeight = new JTextField("" + Constants.ROCK_WEIGHT);
		textField_maxDirtHeight.setColumns(10);
		panel_12.add(textField_maxDirtHeight);

		textField_waterHeight = new JTextField("" + Constants.WATER_HEIGHT);
		textField_waterHeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblWater.setText("Water: "+textField_waterHeight.getText());
			}
		});
		textField_waterHeight.setColumns(10);
		panel_12.add(textField_waterHeight);

		textField_cliffRatio = new JTextField("" + Constants.CLIFF_RATIO);
		panel_12.add(textField_cliffRatio);
		textField_cliffRatio.setColumns(10);

		checkBox_landSlide = new JCheckBox("Land Slide");
		checkBox_landSlide.setToolTipText("Pushes dirt down that is above max slope");
		checkBox_landSlide.setSelected(false);
		panel_12.add(checkBox_landSlide);

		checkbox_biomeRandomSeed = new JCheckBox("Random Seed");
		checkbox_biomeRandomSeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField_biomeSeed.setEnabled(!checkbox_biomeRandomSeed.isSelected());
			}
		});
		panel_12.add(checkbox_biomeRandomSeed);
		checkbox_biomeRandomSeed.setSelected(true);

		JSeparator separator_1 = new JSeparator();
		panel_12.add(separator_1);

		btnGenerateRivers = new JButton("Generate Rivers");
		btnGenerateRivers.setToolTipText("Alters the heightmap");
		panel_12.add(btnGenerateRivers);

		btnResetRivers = new JButton("Reset Painting");
		btnResetRivers.setToolTipText("Clear the currently drawn rivers");
		btnResetRivers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapPanel.clearRiverSeeds();
			}
		});

		btnUndoRiver = new JButton("Undo River");
		panel_12.add(btnUndoRiver);
		panel_12.add(btnResetRivers);

		textField_riverDepth = new JTextField("" + Constants.RIVER_DEPTH);
		panel_12.add(textField_riverDepth);
		textField_riverDepth.setColumns(10);

		textField_riverWidth = new JTextField("" + Constants.RIVER_WIDTH);
		panel_12.add(textField_riverWidth);
		textField_riverWidth.setColumns(10);

		textField_riverSlope = new JTextField("" + Constants.RIVER_SLOPE);
		panel_12.add(textField_riverSlope);
		textField_riverSlope.setColumns(10);

		JPanel panel_13 = new JPanel();

		btnDropDirt = new JButton("Drop Dirt");
		panel_13.add(btnDropDirt);
		GroupLayout gl_dropDirtPanel = new GroupLayout(dropDirtPanel);
		gl_dropDirtPanel.setHorizontalGroup(
				gl_dropDirtPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dropDirtPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_dropDirtPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_9, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_13, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_dropDirtPanel.setVerticalGroup(
				gl_dropDirtPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_dropDirtPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_13, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_9, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);

		btnUpdateWater = new JButton("Update Water");
		panel_13.add(btnUpdateWater);
		dropDirtPanel.setLayout(gl_dropDirtPanel);
		JPanel biomePanel = new JPanel();
		optionsPane.addTab("Biomes", null, biomePanel, null);

		JPanel panel_14 = new JPanel();

		JPanel panel_8 = new JPanel();
		panel_14.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));

		JPanel panel_15 = new JPanel();
		panel_8.add(panel_15);
		panel_15.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_16 = new JPanel();
		panel_15.add(panel_16);
		panel_16.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblSeedCount = new JLabel("Seed Count");
		lblSeedCount.setToolTipText("Amount of biomes to add to the map");
		panel_16.add(lblSeedCount);

		JLabel label_5 = new JLabel("");
		panel_16.add(label_5);

		JLabel lblBiomeSize = new JLabel("Biome Size");
		lblBiomeSize.setToolTipText("How big the biome should grow");
		panel_16.add(lblBiomeSize);

		JLabel lblBiomeDensity = new JLabel("Biome Density");
		lblBiomeDensity.setToolTipText("Higher = more sparse biome");
		panel_16.add(lblBiomeDensity);


		JLabel lblMaxSlope = new JLabel("Max Slope");
		lblMaxSlope.setToolTipText("Don't grow above this slope");
		panel_16.add(lblMaxSlope);

		JLabel lblMinHeight = new JLabel("Min Height");
		lblMinHeight.setToolTipText("Negative offset if around water is set");
		panel_16.add(lblMinHeight);

		JLabel lblMaxHeight_1 = new JLabel("Max Height");
		lblMaxHeight_1.setToolTipText("Positive offset if around water is checked");
		panel_16.add(lblMaxHeight_1);

		lblWater = new JLabel("Water: "+textField_waterHeight.getText());
		lblWater.setToolTipText("Current water height of the map");
		panel_16.add(lblWater);

		JLabel lblGrowth = new JLabel("Growth %");
		lblGrowth.setToolTipText("Chance for biome to grow in a particular direction");
		panel_16.add(lblGrowth);

		JLabel lblNorth = new JLabel(" - North / South");
		panel_16.add(lblNorth);

		JLabel lblEast = new JLabel(" - East / West");
		panel_16.add(lblEast);

		JLabel label_3 = new JLabel("");
		panel_16.add(label_3);

		JLabel lblRandomMin = new JLabel("Growth Min");
		lblRandomMin.setToolTipText("Lower limit of random growth");
		panel_16.add(lblRandomMin);

		JLabel lblRandomMax = new JLabel("Growth Max");
		lblRandomMax.setToolTipText("Upper limit of random growth");
		panel_16.add(lblRandomMax);

		JLabel lblFlowerType = new JLabel("Flower Type");
		panel_16.add(lblFlowerType);

		JLabel lblFlowerPercent = new JLabel("Flower Percent");
		panel_16.add(lblFlowerPercent);

		JPanel panel_17 = new JPanel();
		panel_15.add(panel_17);
		panel_17.setLayout(new GridLayout(0, 1, 0, 2));

		textField_seedCount = new JTextField("" + Constants.BIOME_SEEDS);
		textField_seedCount.setColumns(10);
		panel_17.add(textField_seedCount);

		checkbox_paintMode = new JCheckBox("Paint Mode");
		checkbox_paintMode.setToolTipText("Click on map to plant seed");
		checkbox_paintMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapPanel.setPaintingMode(checkbox_paintMode.isSelected());
				if (checkbox_paintMode.isSelected()) {
					textField_seedCount.setEnabled(false);
				} else {
					textField_seedCount.setEnabled(true);
				}
			}
		});
		panel_17.add(checkbox_paintMode);

		textField_biomeSize = new JTextField("" + Constants.BIOME_SIZE);
		textField_biomeSize.setColumns(10);
		panel_17.add(textField_biomeSize);

		textField_biomeDensity = new JTextField("" + Constants.BIOME_DENSITY);
		panel_17.add(textField_biomeDensity);
		textField_biomeDensity.setColumns(10);

		textField_biomeMaxSlope = new JTextField("" + Constants.BIOME_MAX_SLOPE);
		textField_biomeMaxSlope.setColumns(10);
		panel_17.add(textField_biomeMaxSlope);

		textField_biomeMinHeight = new JTextField("" + Constants.BIOME_MIN_HEIGHT);
		panel_17.add(textField_biomeMinHeight);
		textField_biomeMinHeight.setColumns(10);

		textField_biomeMaxHeight = new JTextField("" + Constants.BIOME_MAX_HEIGHT);
		textField_biomeMaxHeight.setColumns(10);
		panel_17.add(textField_biomeMaxHeight);

		chckbxAroundWater = new JCheckBox("Around Water (-/+)", true);
		panel_17.add(chckbxAroundWater);

		JLabel label_6 = new JLabel("");
		panel_17.add(label_6);

		JPanel panel_29 = new JPanel();
		panel_17.add(panel_29);
		panel_29.setLayout(new GridLayout(0, 2, 0, 0));

		textField_growthE = new JTextField("" + (int) (Constants.BIOME_RATE * 0.6));
		panel_29.add(textField_growthE);
		textField_growthE.setEnabled(false);
		textField_growthE.setColumns(4);

		textField_growthW = new JTextField("" + Constants.BIOME_RATE);
		panel_29.add(textField_growthW);
		textField_growthW.setEnabled(false);
		textField_growthW.setColumns(4);

		JPanel panel_30 = new JPanel();
		panel_17.add(panel_30);
		panel_30.setLayout(new GridLayout(0, 2, 0, 0));

		textField_growthN = new JTextField("" + Constants.BIOME_RATE / 2);
		panel_30.add(textField_growthN);
		textField_growthN.setEnabled(false);
		textField_growthN.setColumns(4);

		textField_growthS = new JTextField("" + (int) (Constants.BIOME_RATE * 1.3));
		panel_30.add(textField_growthS);
		textField_growthS.setEnabled(false);
		textField_growthS.setColumns(4);

		checkbox_growthRandom = new JCheckBox("Randomize");
		checkbox_growthRandom.setToolTipText("Randomly determine growth chance for each direction");
		checkbox_growthRandom.setSelected(true);
		checkbox_growthRandom.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (checkbox_growthRandom.isSelected())
				{
					textField_growthN.setEnabled(false);
					textField_growthS.setEnabled(false);
					textField_growthE.setEnabled(false);
					textField_growthW.setEnabled(false);
					textField_growthMin.setEnabled(true);
					textField_growthMax.setEnabled(true);
				} else
				{
					textField_growthN.setEnabled(true);
					textField_growthS.setEnabled(true);
					textField_growthE.setEnabled(true);
					textField_growthW.setEnabled(true);
					textField_growthMin.setEnabled(false);
					textField_growthMax.setEnabled(false);
				}
			}
		});
		panel_17.add(checkbox_growthRandom);

		textField_growthMin = new JTextField("" + Constants.BIOME_RANDOM_MIN);
		panel_17.add(textField_growthMin);
		textField_growthMin.setColumns(10);

		textField_growthMax = new JTextField("" + Constants.BIOME_RANDOM_MAX);
		panel_17.add(textField_growthMax);
		textField_growthMax.setColumns(10);

		comboBox_FlowerType = new JComboBox(new String[]{"Random","None","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"});
		panel_17.add(comboBox_FlowerType);

		textField_FlowerPercent = new JTextField("" + Constants.BIOME_FLOWER_PERCENT);
		panel_17.add(textField_FlowerPercent);
		textField_FlowerPercent.setColumns(10);


		JPanel panel_27 = new JPanel();
		panel_8.add(panel_27, BorderLayout.NORTH);

		btnImportBiomes = new JButton("Import");
		panel_27.add(btnImportBiomes);

		//		ArrayList<Tile> tiles = new ArrayList<Tile>();
		//		for (Tile tile:Tile.getTiles()) {
		//			if (tile == null) {
		//				continue;
		//			}
		//			if (TileMap.getTileColor(tile) != null) {
		//				tiles.add(tile);
		//			}
		//		}
		//		comboBox_biomeType = new JComboBox(tiles.toArray());
		
		//TODO track new types
		comboBox_biomeType = new JComboBox(new Tile[] { Tile.TILE_CLAY, Tile.TILE_DIRT, Tile.TILE_DIRT_PACKED, Tile.TILE_GRASS, Tile.TILE_GRAVEL, Tile.TILE_KELP,
				Tile.TILE_LAVA, Tile.TILE_MARSH, Tile.TILE_MOSS, Tile.TILE_MYCELIUM, Tile.TILE_PEAT, Tile.TILE_REED, Tile.TILE_SAND, Tile.TILE_STEPPE, 
				Tile.TILE_TAR, Tile.TILE_TUNDRA, Tile.TILE_TREE_APPLE, Tile.TILE_TREE_BIRCH, Tile.TILE_TREE_CEDAR, Tile.TILE_TREE_CHERRY, Tile.TILE_TREE_CHESTNUT, 
				Tile.TILE_TREE_FIR, Tile.TILE_TREE_LEMON, Tile.TILE_TREE_LINDEN, Tile.TILE_TREE_MAPLE, Tile.TILE_TREE_OAK, Tile.TILE_TREE_OLIVE, Tile.TILE_TREE_PINE,
				Tile.TILE_TREE_WALNUT, Tile.TILE_TREE_WILLOW, Tile.TILE_TREE_ORANGE, Tile.TILE_BUSH_CAMELLIA, Tile.TILE_BUSH_GRAPE, Tile.TILE_BUSH_LAVENDER, Tile.TILE_BUSH_OLEANDER,
				Tile.TILE_BUSH_ROSE, Tile.TILE_BUSH_THORN, Tile.TILE_BUSH_HAZELNUT, Tile.TILE_BUSH_RASPBERRYE, Tile.TILE_BUSH_BLUEBERRY, Tile.TILE_BUSH_LINGONBERRY,
				Tile.TILE_TREE, Tile.TILE_BUSH, Tile.TILE_SNOW
		});
		
		panel_27.add(comboBox_biomeType);
		comboBox_biomeType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField_seedCount.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][0]);              
				textField_biomeSize.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][1]);              
				textField_biomeMaxSlope.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][2]);              
				textField_growthN.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][3]);              
				textField_growthS.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][4]);              
				textField_growthE.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][5]);              
				textField_growthW.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][6]);              
				textField_biomeMinHeight.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][7]);              
				textField_biomeMaxHeight.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][8]);  

				checkbox_growthRandom.setSelected(!Boolean.parseBoolean(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][9]));
				checkbox_growthRandom.doClick();

				textField_growthMin.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][10]);              
				textField_growthMax.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][11]);  

				chckbxAroundWater.setSelected(Boolean.parseBoolean(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][12]));
				textField_biomeDensity.setText(biomeOptionValue[comboBox_biomeType.getSelectedIndex()][13]);

				if (comboBox_biomeType.getSelectedItem() == Tile.TILE_GRASS) {
					comboBox_FlowerType.setEnabled(true);
					textField_FlowerPercent.setEnabled(true);
				} else {
					comboBox_FlowerType.setEnabled(false);
					textField_FlowerPercent.setEnabled(false);
				}
			}
		});                
		JPanel panel_18 = new JPanel();
		panel_18.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_31 = new JPanel();
		panel_18.add(panel_31);

		btnAddBiome = new JButton("Add Biome");
		panel_31.add(btnAddBiome);

		btnUndoLastBiome = new JButton("Undo Last");
		panel_31.add(btnUndoLastBiome);
		btnUndoLastBiome.setToolTipText("Can only go back 1 action");

		btnResetBiomes = new JButton("Reset All");
		panel_31.add(btnResetBiomes);
		GroupLayout gl_biomePanel = new GroupLayout(biomePanel);
		gl_biomePanel.setHorizontalGroup(
				gl_biomePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_biomePanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_biomePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_14, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_18, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_biomePanel.setVerticalGroup(
				gl_biomePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_biomePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_18, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_14, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);
		biomePanel.setLayout(gl_biomePanel);

		JPanel orePanel = new JPanel();
		optionsPane.addTab("Ores", null, orePanel, null);
		optionsPane.setEnabledAt(4, true);

		JPanel panel_19 = new JPanel();

		JPanel panel_20 = new JPanel();
		panel_19.add(panel_20);
		panel_20.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_21 = new JPanel();
		panel_20.add(panel_21);
		panel_21.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblIron = new JLabel("Iron");
		panel_21.add(lblIron);

		JLabel lblGold = new JLabel("Gold");
		panel_21.add(lblGold);

		JLabel lblSilver = new JLabel("Silver");
		panel_21.add(lblSilver);

		JLabel lblZinc = new JLabel("Zinc");
		panel_21.add(lblZinc);

		JLabel lblCopper = new JLabel("Copper");
		panel_21.add(lblCopper);

		JLabel lblLead = new JLabel("Lead");
		panel_21.add(lblLead);

		JLabel lblTin = new JLabel("Tin");
		panel_21.add(lblTin);

		JLabel lblMarble = new JLabel("Marble");
		panel_21.add(lblMarble);

		JLabel lblSlate = new JLabel("Slate");
		panel_21.add(lblSlate);
		
		JLabel lblSandstone = new JLabel("Sandstone");
		panel_21.add(lblSandstone);
		
		JLabel lblRocksalt = new JLabel("Rocksalt");
		panel_21.add(lblRocksalt);

		JLabel lblAddy = new JLabel("Adamantine");
		panel_21.add(lblAddy);

		JLabel lblGlimmer = new JLabel("Glimmer");
		panel_21.add(lblGlimmer);

		JLabel lblRock = new JLabel("Rock");
		lblRock.setToolTipText("Left over amount, adds up to 100%");
		panel_21.add(lblRock);

		JPanel panel_22 = new JPanel();
		panel_20.add(panel_22);
		panel_22.setLayout(new GridLayout(0, 1, 0, 2));

		textField_Iron = new JTextField("" + Constants.ORE_IRON);
		textField_Iron.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Iron.setColumns(4);
		panel_22.add(textField_Iron);

		textField_Gold = new JTextField("" + Constants.ORE_GOLD);
		textField_Gold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Gold.setColumns(4);
		panel_22.add(textField_Gold);

		textField_Silver = new JTextField("" + Constants.ORE_SILVER);
		textField_Silver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Silver.setColumns(4);
		panel_22.add(textField_Silver);

		textField_Zinc = new JTextField("" + Constants.ORE_ZINC);
		textField_Zinc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Zinc.setColumns(4);
		panel_22.add(textField_Zinc);

		textField_Copper = new JTextField("" + Constants.ORE_COPPER);
		textField_Copper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Copper.setColumns(4);
		panel_22.add(textField_Copper);

		textField_Lead = new JTextField("" + Constants.ORE_LEAD);
		textField_Lead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		panel_22.add(textField_Lead);
		textField_Lead.setColumns(4);

		textField_Tin = new JTextField("" + Constants.ORE_TIN);
		textField_Tin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Tin.setColumns(4);
		panel_22.add(textField_Tin);

		textField_Marble = new JTextField("" + Constants.ORE_MARBLE);
		textField_Marble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Marble.setColumns(4);
		panel_22.add(textField_Marble);

		textField_Slate = new JTextField("" + Constants.ORE_SLATE);
		textField_Slate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Slate.setColumns(4);
		panel_22.add(textField_Slate);

		textField_Sandstone = new JTextField("" + Constants.ORE_SANDSTONE);
		textField_Sandstone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Sandstone.setColumns(4);
		panel_22.add(textField_Sandstone);
		
		textField_Rocksalt = new JTextField("" + Constants.ORE_ROCKSALT);
		textField_Rocksalt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Rocksalt.setColumns(4);
		panel_22.add(textField_Rocksalt);
		
		textField_Addy = new JTextField("" + Constants.ORE_ADDY);
		textField_Addy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Addy.setColumns(4);
		panel_22.add(textField_Addy);

		textField_Glimmer = new JTextField("" + Constants.ORE_GLIMMER);
		textField_Glimmer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Glimmer.setColumns(4);
		panel_22.add(textField_Glimmer);

		textField_Rock = new JTextField("");
		textField_Rock.setEditable(false);
		textField_Rock.setColumns(4);
		panel_22.add(textField_Rock);

		JPanel panel_23 = new JPanel();

		btnGenerateOres = new JButton("Generate Ores");
		panel_23.add(btnGenerateOres);
		GroupLayout gl_orePanel = new GroupLayout(orePanel);
		gl_orePanel.setHorizontalGroup(
				gl_orePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_orePanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_orePanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_19, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_23, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_orePanel.setVerticalGroup(
				gl_orePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_orePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_23, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_19, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);
		orePanel.setLayout(gl_orePanel);

		JPanel actionPanel = new JPanel();
		optionsPane.addTab("Actions / Export", null, actionPanel, null);
		optionsPane.setEnabledAt(5, true);

		JPanel panel_24 = new JPanel();

		JPanel panel_26 = new JPanel();
		panel_24.add(panel_26);
		panel_26.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblNewLabel_2 = new JLabel("Map Name");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_26.add(lblNewLabel_2);

		textField_mapName = new JTextField(textField_mapSeed.getText());
		mapName = textField_mapSeed.getText();
		panel_26.add(textField_mapName);
		textField_mapName.setColumns(10);

		btnUpdateMapName = new JButton("Update Map Name");
		panel_26.add(btnUpdateMapName);

		JLabel label_1 = new JLabel("");
		panel_26.add(label_1);

		btnSaveImageDumps = new JButton("Save Images");
		panel_26.add(btnSaveImageDumps);

		btnSaveMapFiles = new JButton("Save Map Files");
		panel_26.add(btnSaveMapFiles);

		btnSaveActions = new JButton("Save Actions");

		JLabel label_2 = new JLabel("");
		panel_26.add(label_2);
		panel_26.add(btnSaveActions);

		btnLoadActions = new JButton("Load Actions");
		btnLoadActions.setEnabled(true);
		panel_26.add(btnLoadActions);

		JButton btnClearActions = new JButton("Clear Actions");
		btnClearActions.setToolTipText("Reset action history");
		btnClearActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				genHistory.clear();
			}
		});
		panel_26.add(btnClearActions);

		btnExportBiomes = new JButton("Export Biome Values");

		JButton btnSaveGlobalBiomes = new JButton("Save Global Values");
		btnSaveGlobalBiomes.setToolTipText("Values auto loaded at startup");
		btnSaveGlobalBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionSaveGlobalBiomeValues();
			}
		});

		JLabel label_9 = new JLabel("");
		panel_26.add(label_9);

		JSeparator separator_2 = new JSeparator();
		panel_26.add(separator_2);
		panel_26.add(btnSaveGlobalBiomes);
		btnExportBiomes.setToolTipText("Save biome input values to config file");
		panel_26.add(btnExportBiomes);

		btnLoadBiomes = new JButton("Load Biome Values");
		panel_26.add(btnLoadBiomes);
		GroupLayout gl_actionPanel = new GroupLayout(actionPanel);
		gl_actionPanel.setHorizontalGroup(
				gl_actionPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_actionPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_24, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
						.addContainerGap())
				);
		gl_actionPanel.setVerticalGroup(
				gl_actionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_actionPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_24, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addContainerGap())
				);
		actionPanel.setLayout(gl_actionPanel);
		contentPane.setLayout(gl_contentPane);

		init();
	}


	private void init() {
		setupButtonActions();
		setRockTotal();
		updateMapCoords(0,0,false);
		progress = new ProgressHandler(progressBar,lblMemory, taskbar, this);
		progress.update(100);
		System.setErr(new PrintStream(new StreamCapturer(System.err,this)));

		// Loads biome input values from config file
		try {
			(new File(Constants.CONFIG_DIRECTORY)).mkdirs();
			FileReader fr = new FileReader(Constants.CONFIG_DIRECTORY+"biome_values.txt");
			BufferedReader br = new BufferedReader(fr); 
			String s;

			for (int bt = 0; bt < biomeOptionValue.length; bt++) {
				s = br.readLine();
				if (s!=null) {
					String[] parts = s.split(",");
					for (int bv = 0; bv < 14; bv++) {
						biomeOptionValue[bt][bv] = parts[bv];
					}
				} 
			}
			fr.close();
		}
		catch (IOException e) {} //File doesn't exist
		comboBox_biomeType.setSelectedIndex(12);
	}

	private void setupButtonActions() {

		btnUpdateMapName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (heightMap == null) {
					JOptionPane.showMessageDialog(null, "Heightmap does not exist - Generate one first", "Error Saving Map Api", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (tileMap == null) {
					JOptionPane.showMessageDialog(null, "Tile map does not exist - Generate one first", "Error Saving Map Api", JOptionPane.ERROR_MESSAGE);
					return;
				}
				new Thread() {
					@Override
					public void run() {
						mapName = textField_mapName.getText();
						if(mapName.equals("")) {
							textField_mapName.setText("empty");
							mapName = "empty";
						}
						if (!apiClosed) {
							getAPI().close();
						}
						apiClosed = true;
						updateAPIMap();
					}
				}.start();
			}
		});
		btnViewMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cl_mainPanel.show(mainPanel,"MAP");
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewMap();
					}
				}.start();
			}
		});
		btnViewTopo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl_mainPanel.show(mainPanel,"MAP");
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewTopo();
					}
				}.start();
			}
		});
		btnViewBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl_mainPanel.show(mainPanel,"MAP");
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewBiomes();
					}
				}.start();
			}
		});
		btnViewCave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl_mainPanel.show(mainPanel,"MAP");
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewCave();
					}
				}.start();
			}
		});
		btnViewHeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl_mainPanel.show(mainPanel,"MAP");
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewHeightmap();
					}
				}.start();
			}
		});
		btnGenerateHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionGenerateHeightmap();
					}
				}.start();
			}
		});
		btnErodeHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionErodeHeightmap();
					}
				}.start();
			}
		});
		btnDropDirt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionDropDirt();
					}
				}.start();
			}
		});
		btnUpdateWater.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionUpdateWater();
					}
				}.start();
			}
		});
		btnGenerateRivers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionGenerateRivers();
					}
				}.start();
			}
		});
		btnUndoRiver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionUndoRiver();
					}
				}.start();
			}
		});
		btnAddBiome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSeedBiome(null);
					}
				}.start();
			}
		});
		btnUndoLastBiome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionUndoBiome();
					}
				}.start();
			}
		});
		btnResetBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionResetBiomes();
					}
				}.start();
			}
		});
		btnGenerateOres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionGenerateOres();
					}
				}.start();
			}
		});
		btnSaveImageDumps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveImages();
					}
				}.start();
			}
		});
		btnSaveMapFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveMap();
					}
				}.start();
			}
		});
		btnSaveActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveActions();
					}
				}.start();
			}
		});
		btnLoadActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionLoadActions();
					}
				}.start();
			}
		});
		btnLoadHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionLoadHeightmap();
					}
				}.start();
			}
		});
		btnImportBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionLoadBiomes();
					}
				}.start();
			}
		});
		btnExportBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveBiomeValues();;
					}
				}.start();
			}
		});
		btnLoadBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionLoadBiomeValues();;
					}
				}.start();
			}
		});

	}

	private void startLoading(String task) {
		progress.update(0, task);
	}

	private void stopLoading() {
		progress.update(100, Constants.READY);
		taskbar.setWindowProgressValue(this, 0);
	}

	boolean actionReady() {
		return progressBar.getValue() == 100;
	}


	void actionGenerateHeightmap () {

		startLoading("Generating Height Map ()");
		try {
			api = null;
			genHistory = new ArrayList<String>();

			if (checkbox_mapRandomSeed.isSelected()) {
				textField_mapSeed.setText("" + System.currentTimeMillis());
			}

			mapPanel.setMapSize((int) comboBox_mapSize.getSelectedItem());

			heightMap = new HeightMap(textField_mapSeed.getText().hashCode(), (int) comboBox_mapSize.getSelectedItem(), 
					Double.parseDouble(textField_mapResolution.getText()), Integer.parseInt(textField_mapIterations.getText()), 
					Integer.parseInt(textField_mapMinEdge.getText()), Integer.parseInt(textField_mapBorderWeight.getText()), 
					Integer.parseInt(textField_mapMaxHeight.getText()), Integer.parseInt(textField_normalizeRatio.getText()), checkbox_moreLand.isSelected());

			heightMap.generateHeights(progress);

			defaultView = Constants.VIEW_TYPE.HEIGHT;
			updateMapView();

			genHistory.add("HEIGHTMAP:" + textField_mapSeed.getText() + "," + comboBox_mapSize.getSelectedIndex() + "," + textField_mapResolution.getText() + "," +
					textField_mapIterations.getText() + "," + textField_mapMinEdge.getText() + "," + textField_mapBorderWeight.getText() + "," +
					textField_mapMaxHeight.getText() + "," + textField_normalizeRatio.getText() + "," + checkbox_moreLand.isSelected());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
		} catch (InterruptedException ie) {
			JOptionPane.showMessageDialog(null, "Multithreading interrupted " + ie.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionErodeHeightmap () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Eroding Height Map ()");
		try {
			heightMap.erode(Integer.parseInt(textField_erodeIterations.getText()), Integer.parseInt(textField_erodeMinSlope.getText()), 
					Integer.parseInt(textField_erodeMaxSlope.getText()), Integer.parseInt(textField_erodeSediment.getText()), progress);

			updateMapView();

			genHistory.add("ERODE:" + textField_erodeIterations.getText() + "," + textField_erodeMinSlope.getText() + "," + textField_erodeSediment.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionDropDirt () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Dropping Dirt ()");
		try {
			if (checkbox_biomeRandomSeed.isSelected()) {
				textField_biomeSeed.setText("" + System.currentTimeMillis());
			}
			lblWater.setText("Water: "+textField_waterHeight.getText());

			tileMap = new TileMap(heightMap);
			tileMap.setBiomeSeed(textField_biomeSeed.getText().hashCode());
			tileMap.setWaterHeight(Integer.parseInt(textField_waterHeight.getText()));
			tileMap.dropDirt(Integer.parseInt(textField_dirtPerTile.getText()), Integer.parseInt(textField_maxDirtSlope.getText()), 
					Integer.parseInt(textField_maxDiagSlope.getText()), Integer.parseInt(textField_maxDirtHeight.getText()), 
					Double.parseDouble(textField_cliffRatio.getText()), checkBox_landSlide.isSelected(), progress);

			if (defaultView == Constants.VIEW_TYPE.HEIGHT) {
				defaultView = Constants.VIEW_TYPE.ISO;
			}
			updateMapView();

			genHistory.add("DROPDIRT:" + textField_biomeSeed.getText() + "," + textField_waterHeight.getText() + "," + textField_dirtPerTile.getText() + "," +
					textField_maxDirtSlope.getText() + "," + textField_maxDiagSlope.getText() + "," + textField_maxDirtHeight.getText() + "," + 
					Double.parseDouble(textField_cliffRatio.getText()) + "," + checkBox_landSlide.isSelected());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionUpdateWater () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Updating Water", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Updating Water", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Updating Water");
		try {
			lblWater.setText("Water: "+textField_waterHeight.getText());
			tileMap.setWaterHeight(Integer.parseInt(textField_waterHeight.getText()));

			updateMapView();

			genHistory.add("UPDATEWATER:" + textField_waterHeight.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Updating Water", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionGenerateRivers () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Generating River", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Generating Rivers");
		try {
			heightMap.exportHeightImage(mapName, "river_heightmap.png");
			double water = (Integer.parseInt(textField_waterHeight.getText())-Integer.parseInt(textField_dirtPerTile.getText())-
					Integer.parseInt(textField_riverDepth.getText()))/(double)Integer.parseInt(textField_mapMaxHeight.getText());
			for (Point p:mapPanel.getRiverSeeds()) {
				heightMap.createPond(p.x,p.y,water,Integer.parseInt(textField_riverWidth.getText()),Integer.parseInt(textField_riverSlope.getText()));
			}

			mapPanel.setRiverPaintingMode(false);
			checkbox_paintRivers.setSelected(false);
			mapPanel.clearRiverSeeds();

			if (checkbox_autoDropDirt.isSelected()) {
				actionDropDirt();
			}

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating River", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionUndoRiver () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Undoing River", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Undoing River");
		try {
			Constants.VIEW_TYPE oldView = defaultView;
			File heightImageFile = new File("./maps/" + mapName +"/river_heightmap.png");
			defaultView = Constants.VIEW_TYPE.HEIGHT;
			actionLoadHeightmap(heightImageFile);

			if (checkbox_autoDropDirt.isSelected()) {
				defaultView = oldView;
				actionDropDirt();
			}

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Undoing River", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionSeedBiome (Point origin) {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Adding Biome", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Seeding Biome");
		try {
			// Save the edited fields back into the biome input table
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][0] = textField_seedCount.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][1] = textField_biomeSize.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][2] = textField_biomeMaxSlope.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][3] = textField_growthN.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][4] = textField_growthS.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][5] = textField_growthE.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][6] = textField_growthW.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][7] = textField_biomeMinHeight.getText();              
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][8] = textField_biomeMaxHeight.getText();    
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][9] = Boolean.toString(checkbox_growthRandom.isSelected());
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][10] = textField_growthMin.getText();
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][11] = textField_growthMax.getText();
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][12] = Boolean.toString(chckbxAroundWater.isSelected());
			biomeOptionValue[comboBox_biomeType.getSelectedIndex()][13] = textField_biomeDensity.getText();    


			int[] rates = new int[4];
			if (checkbox_growthRandom.isSelected()) {
				int min = Integer.parseInt(textField_growthMin.getText());
				int max = Integer.parseInt(textField_growthMax.getText());
				if(min >= max) {
					min = max-1;
				}
				rates[0] = min;
				rates[1] = max;
				rates[2] = 0;
				rates[3] = 0;
			} else {
				rates[0] = Integer.parseInt(textField_growthN.getText());
				rates[1] = Integer.parseInt(textField_growthS.getText()); 
				rates[2] = Integer.parseInt(textField_growthE.getText()); 
				rates[3] = Integer.parseInt(textField_growthW.getText()); 
			}

			if (Integer.parseInt(textField_biomeDensity.getText()) < 1) {
				textField_biomeDensity.setText("1");
			}
			if (Integer.parseInt(textField_biomeMinHeight.getText()) < 0) {
				textField_biomeMinHeight.setText("0");
			}
			if (Integer.parseInt(textField_biomeMaxHeight.getText()) > Integer.parseInt(textField_maxDirtHeight.getText())) {
				textField_biomeMaxHeight.setText(textField_maxDirtHeight.getText());
			}

			int minHeight = chckbxAroundWater.isSelected()? Integer.parseInt(textField_waterHeight.getText())-Integer.parseInt(textField_biomeMinHeight.getText())
					: Integer.parseInt(textField_biomeMinHeight.getText());
			int maxHeight = chckbxAroundWater.isSelected()? Integer.parseInt(textField_waterHeight.getText())+Integer.parseInt(textField_biomeMaxHeight.getText())
			: Integer.parseInt(textField_biomeMaxHeight.getText());

			if (origin == null) {
				tileMap.plantBiome(Integer.parseInt(textField_seedCount.getText()), Integer.parseInt(textField_biomeSize.getText()), Integer.parseInt(textField_biomeDensity.getText()), 
						rates, checkbox_growthRandom.isSelected(), Integer.parseInt(textField_biomeMaxSlope.getText()), minHeight, maxHeight, (Tile) comboBox_biomeType.getSelectedItem(),
						comboBox_FlowerType.getSelectedIndex(), Integer.parseInt(textField_FlowerPercent.getText()), progress);

				genHistory.add("SEEDBIOME("+comboBox_biomeType.getSelectedItem()+"):" + comboBox_biomeType.getSelectedIndex() + "," + textField_seedCount.getText() + 
						"," + textField_biomeSize.getText() + "," + textField_biomeDensity.getText() + "," + textField_biomeMaxSlope.getText() + "," +
						textField_growthN.getText()+","+textField_growthS.getText()+","+textField_growthE.getText()+","+textField_growthW.getText()+"," +
						checkbox_growthRandom.isSelected() +","+ textField_growthMin.getText()+","+textField_growthMax.getText() +","+
						textField_biomeMinHeight.getText() + "," + textField_biomeMaxHeight.getText() + "," + chckbxAroundWater.isSelected() + "," +
						comboBox_FlowerType.getSelectedIndex() + "," + textField_FlowerPercent.getText());
			} else {
				tileMap.plantBiomeAt(origin.x, origin.y, Integer.parseInt(textField_biomeSize.getText()), Integer.parseInt(textField_biomeDensity.getText()), 
						rates, checkbox_growthRandom.isSelected(), Integer.parseInt(textField_biomeMaxSlope.getText()), minHeight, maxHeight, (Tile) comboBox_biomeType.getSelectedItem(),
						comboBox_FlowerType.getSelectedIndex(), Integer.parseInt(textField_FlowerPercent.getText()), progress);

				genHistory.add("PAINTBIOME("+comboBox_biomeType.getSelectedItem()+"):" + comboBox_biomeType.getSelectedIndex() + "," + origin.x + "," + origin.y + 
						"," + textField_biomeSize.getText() + "," + textField_biomeDensity.getText() + "," + textField_biomeMaxSlope.getText() + "," +
						textField_growthN.getText()+","+textField_growthS.getText()+","+textField_growthE.getText()+","+textField_growthW.getText()+"," +
						checkbox_growthRandom.isSelected() +","+ textField_growthMin.getText()+","+textField_growthMax.getText() +","+
						textField_biomeMinHeight.getText() + "," + textField_biomeMaxHeight.getText() + "," + chckbxAroundWater.isSelected() + "," +
						comboBox_FlowerType.getSelectedIndex() + "," + textField_FlowerPercent.getText());
			}

			updateMapView();


		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionUndoBiome () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Undoing Biome");
		try {

			tileMap.undoLastBiome();

			updateMapView();

			genHistory.add("UNDOBIOME:null");
		} finally {
			stopLoading();
		}
	}

	void actionResetBiomes () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Resetting Biomes");
		try {

			for (int i = 0; i < heightMap.getMapSize(); i++) {
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					int prog = (int)((float)(i*heightMap.getMapSize()+j)/(heightMap.getMapSize()*heightMap.getMapSize())*100f);
					taskbar.setWindowProgressValue(this, prog);
					progress.update(prog);
					tileMap.addDirt(i, j, 0);
				}
			}
			
			updateMapView();

			genHistory.add("RESETBIOMES:null"); 
		} finally {
			stopLoading();
		}
	}

	void actionGenerateOres () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Generating Ores");
		try {
			setRockTotal();
			if (Double.parseDouble(textField_Rock.getText()) < 0.0 || Double.parseDouble(textField_Rock.getText()) > 100.0) {
				JOptionPane.showMessageDialog(null, "Ore values out of range", "Error Generating Ore", JOptionPane.ERROR_MESSAGE);
				return;
			}

			double[] rates = { Double.parseDouble(textField_Rock.getText()), Double.parseDouble(textField_Iron.getText()), Double.parseDouble(textField_Gold.getText()),
					Double.parseDouble(textField_Silver.getText()), Double.parseDouble(textField_Zinc.getText()), Double.parseDouble(textField_Copper.getText()),
					Double.parseDouble(textField_Lead.getText()), Double.parseDouble(textField_Tin.getText()), Double.parseDouble(textField_Addy.getText()),
					Double.parseDouble(textField_Glimmer.getText()), Double.parseDouble(textField_Marble.getText()), Double.parseDouble(textField_Slate.getText()),
					Double.parseDouble(textField_Sandstone.getText()), Double.parseDouble(textField_Rocksalt.getText())
			};

			tileMap.generateOres(rates, progress);

			defaultView = Constants.VIEW_TYPE.CAVE;
			updateMapView();

			genHistory.add("GENORES:" + textField_Rock.getText() + "," + textField_Iron.getText() + "," + textField_Gold.getText() + "," +
					textField_Silver.getText() + "," + textField_Zinc.getText() + "," + textField_Copper.getText() + "," +
					textField_Lead.getText() + "," + textField_Tin.getText() + "," + textField_Addy.getText() + "," +
					textField_Glimmer.getText() + "," + textField_Marble.getText() + "," + textField_Slate.getText() + "," + 
					textField_Sandstone.getText() + "," + textField_Rocksalt.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionViewMap () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			defaultView = Constants.VIEW_TYPE.ISO;
			updateMapView();
		} finally {
			stopLoading();
		}
	}

	void actionViewTopo () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			defaultView = Constants.VIEW_TYPE.TOPO;
			updateMapView();
		} finally {
			stopLoading();
		}
	}

	void actionViewBiomes () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			defaultView = Constants.VIEW_TYPE.BIOMES;
			updateMapView();
		} finally {
			stopLoading();
		}
	}

	void actionViewCave () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!tileMap.hasOres()) {
			JOptionPane.showMessageDialog(null, "No Cave Map - Generate Ores first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			defaultView = Constants.VIEW_TYPE.CAVE;
			updateMapView();
		} finally {
			stopLoading();
		}
	}

	void actionViewHeightmap () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			defaultView = Constants.VIEW_TYPE.HEIGHT;
			updateMapView();
		} finally {
			stopLoading();
		}
	}

	void actionSaveImages () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Saving Images", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Saving Images");
		try {
			updateAPIMap();
			MapData map = getAPI().getMapData();
			ImageIO.write(map.createMapDump(), "png", new File("./maps/" + mapName + "/map.png"));
			ImageIO.write(map.createTopographicDump(true, (short) 250), "png", new File("./maps/" + mapName + "/topography.png"));
			ImageIO.write(map.createCaveDump(true), "png", new File("./maps/" + mapName + "/cave.png"));

			heightMap.exportHeightImage(mapName, "heightmap.png");
			saveBiomesImage();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			stopLoading();
		}
	}

	private void saveBiomesImage () {
		try {
			BufferedImage bufferedImage = getBiomeImage();

			File imageFile = new File("./maps/" + mapName + "/" + "biomes.png");
			if (!imageFile.exists()) {
				imageFile.mkdirs();
			}
			ImageIO.write(bufferedImage, "png", imageFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	void actionSaveMap () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Saving Map");
		try {
			updateAPIMap();
			getAPI().getMapData().saveChanges();
		} finally {
			stopLoading();
		}
	}

	void actionSaveGlobalBiomeValues () {
		try {
			FileWriter fw = new FileWriter(Constants.CONFIG_DIRECTORY+"biome_values.txt");
			for (int bt = 0; bt < biomeOptionValue.length; bt++) {
				for (int bv = 0; bv < biomeOptionValue[0].length; bv++) {
					fw.write(biomeOptionValue[bt][bv]); 
					if (bv<13)
						fw.write(","); 
				}
				fw.write("\r\n"); 
			}
			fw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}    
	}

	void actionSaveBiomeValues () {
		startLoading("Saving Biome Values");
		try {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(Constants.CONFIG_DIRECTORY));
			fc.setSelectedFile(new File("biome_values.txt"));
			fc.setFileFilter(new TextFileView());
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File BiomeValueFile = fc.getSelectedFile();
			BiomeValueFile.createNewFile();

			BufferedWriter bw = new BufferedWriter(new FileWriter(BiomeValueFile));

			String biotxt;
			try {
				FileWriter fw = new FileWriter(BiomeValueFile);
				for (int bt=0;bt<36; bt++){
					for (int bv = 0; bv < 14; bv++) {
						biotxt=biomeOptionValue[bt][bv];
						fw.write(biotxt);
						if (bv<13)
							fw.write(",");
					}
					fw.write("\r\n");
				}
				fw.close();
			}
			catch (IOException ex){
				System.err.println("Saving BiomeValues.txt failed: "+ex.toString());
			}

			bw.close();


		} catch (IOException ex) {
			System.err.println("Saving Biome values failed: "+ex.toString());
		} finally {
			stopLoading();
		}
	}

	public void actionLoadBiomeValues () {
		startLoading("Loading Biome Values");
		try {
			File BiomeValueFile;

			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(Constants.CONFIG_DIRECTORY));
			fc.setFileFilter(new TextFileView());
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showDialog(null, "Load Biome Values");

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				BiomeValueFile = fc.getSelectedFile();
				textField_mapName.setText(BiomeValueFile.getParentFile().getName());
				actionsFileDirectory = BiomeValueFile.getParentFile().getAbsolutePath();

				FileReader fr = new FileReader(BiomeValueFile);
				BufferedReader br = new BufferedReader(fr);

				String s;

				for (int bt = 0; bt < 36; bt++) {
					s = br.readLine();
					if (s!=null) {
						String[] parts = s.split(",");
						for (int bv = 0; bv < 14; bv++) {
							biomeOptionValue[bt][bv]=parts[bv];
						}
					}
				}
				fr.close();
				comboBox_biomeType.setSelectedIndex(12);
			}
		} catch (IOException ex) {
			System.err.println("Loading Biome Values failed: "+ex.toString());
		} finally {
			stopLoading();
		}
	}

	void actionSaveActions () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Saving Actions");
		try {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("./maps/" + mapName));
			fc.setSelectedFile(new File("map_actions.act"));
			fc.setFileFilter(new ActionFileView());
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File actionsFile = fc.getSelectedFile();
			actionsFile.createNewFile();

			BufferedWriter bw = new BufferedWriter(new FileWriter(actionsFile));
			for (String s : genHistory)
				bw.write(s + "\r\n");

			bw.close();

			heightMap.exportHeightImage(mapName, "heightmap.png");
		} catch (IOException ex) {
			System.err.println("Saving actions failed: "+ex.toString());
		} finally {
			stopLoading();
		}
	}

	void actionLoadActions () {

		startLoading("Loading Actions");
		try {
			File actionsFile;

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ActionFileView());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setCurrentDirectory(new File("./maps/"));

			int returnVal = fc.showDialog(null, "Load Actions");

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				actionsFile = fc.getSelectedFile();
				textField_mapName.setText(actionsFile.getParentFile().getName());
				actionsFileDirectory = actionsFile.getParentFile().getAbsolutePath();

				BufferedReader br = new BufferedReader(new FileReader(actionsFile));
				String line;
				while ((line = br.readLine()) != null) {
					parseAction(line);
				}

				br.close();
			}
		} catch (IOException ex) {
			System.err.println("Loading actions failed: "+ex.toString());
		} finally {
			stopLoading();
			//Reset interface
			checkbox_growthRandom.doClick();
			checkbox_growthRandom.doClick();
		}
	}

	void actionLoadHeightmap (File heightImageFile) {
		startLoading("Loading Heightmap");
		try {
			int mapSize = (int)comboBox_mapSize.getSelectedItem();
			api = null;
			genHistory = new ArrayList<String>();
			BufferedImage heightImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
			heightImage = ImageIO.read(heightImageFile);
			mapPanel.setMapSize(mapSize);
			heightMap = new HeightMap(heightImage, mapSize, Integer.parseInt(textField_mapMaxHeight.getText()));

			updateMapView();

			genHistory.add("IMPORTHEIGHTMAP:" + heightImageFile.getName() + "," + comboBox_mapSize.getSelectedIndex() + "," + textField_mapMaxHeight.getText());

		} catch (NumberFormatException | IOException nfe) {
			JOptionPane.showMessageDialog(this, "Error loading file " + nfe.getMessage().toLowerCase(), "Error Loading Heightmap", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	void actionLoadHeightmap () {
		File imageFile;

		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new ImageFileView());
		fc.setAcceptAllFileFilterUsed(false);
		fc.setCurrentDirectory(new File("./maps/"));

		int returnVal = fc.showDialog(this, "Load Heightmap");
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		imageFile = fc.getSelectedFile();

		actionLoadHeightmap(imageFile);
	}

	void actionLoadBiomes () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Loading Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}
		startLoading("Loading Biomes");
		try {
			int mapSize = (int)comboBox_mapSize.getSelectedItem();

			File imageFile;

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ImageFileView());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setCurrentDirectory(new File("./maps/"));

			int returnVal = fc.showDialog(this, "Load Biomes");
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			imageFile = fc.getSelectedFile();
			BufferedImage biomesImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
			biomesImage = ImageIO.read(imageFile);

			tileMap.importBiomeImage(biomesImage);

			updateMapView();

		} catch (NumberFormatException | IOException nfe) {
			JOptionPane.showMessageDialog(this, "Error loading file " + nfe.getMessage().toLowerCase(), "Error Loading Biomes", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}

	}


	private WurmAPI getAPI() {
		if (apiClosed)
			api = null;

		if (api == null)
			try {
				api = WurmAPI.create("./maps/" + mapName + "/", (int) (Math.log(heightMap.getMapSize()) / Math.log(2)));
				apiClosed = false;
			} catch (IOException e) {
				e.printStackTrace();
			}

		return api;
	}


	private void updateMapView() {
		if (defaultView == Constants.VIEW_TYPE.HEIGHT) {
			startLoading("Loading View");
			Graphics g = mapPanel.getMapImage().getGraphics();

			for (int i = 0; i < heightMap.getMapSize(); i++) {
				int prog = (int)((float)i/heightMap.getMapSize()*98f);
				taskbar.setWindowProgressValue(this, prog);
				progress.update(prog);
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					g.setColor(new Color((float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j)));
					g.fillRect(i, j, 1, 1);
				}
			}
		} else {
			updateAPIMap();

			if (defaultView == Constants.VIEW_TYPE.TOPO)
				mapPanel.setMapImage(getAPI().getMapData().createTopographicDump(true, (short) 250));
			else if (defaultView == Constants.VIEW_TYPE.CAVE)
				mapPanel.setMapImage(getAPI().getMapData().createCaveDump(true));
			else if (defaultView == Constants.VIEW_TYPE.ISO)
				mapPanel.setMapImage(getAPI().getMapData().createMapDump());
			else if (defaultView == Constants.VIEW_TYPE.BIOMES)
				mapPanel.setMapImage(getBiomeImage());
		}

		mapPanel.updateScale();
		mapPanel.checkBounds();
		mapPanel.repaint();
		stopLoading();
	}

	private void updateAPIMap() {
		startLoading("Updating Map");
		MapData map = getAPI().getMapData();
		Random treeRand = new Random(System.currentTimeMillis());

		try {
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				int prog = (int)((float)i/heightMap.getMapSize()*100f/3);
				taskbar.setWindowProgressValue(this, prog);
				progress.update(prog);
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					map.setSurfaceHeight(i, j, tileMap.getSurfaceHeight(i, j));
					map.setRockHeight(i, j, tileMap.getRockHeight(i, j));

					if (tileMap.hasOres()) {
						map.setCaveTile(i, j, tileMap.getOreType(i, j), tileMap.getOreCount(i, j));
					}
					map.setSurfaceTile(i, j, Tile.TILE_ROCK);
				}
			}
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				int prog = (int)((float)i/heightMap.getMapSize()*100f/3)+33;
				taskbar.setWindowProgressValue(this, prog);
				progress.update(prog);
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					if(tileMap.getType(i, j) != Tile.TILE_ROCK && !tileMap.getType(i, j).isTree() && !tileMap.getType(i, j).isBush()) {
						for(int x = i - 1; x <= i + 1; x++) {
							for(int y = j - 1; y <= j + 1; y++) {
								if(x > 0 && y > 0 && x < heightMap.getMapSize() && y <heightMap.getMapSize()) {
									map.setSurfaceTile(x, y, tileMap.getType(i, j));
									map.setGrass(x, y, GrowthStage.MEDIUM, FlowerType.fromInt(tileMap.getFlowerType(x, y)));
								}
							}
						}
					}    
				}
			}
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				int prog = (int)((float)i/heightMap.getMapSize()*100f/3)+66;
				taskbar.setWindowProgressValue(this, prog);
				progress.update(prog);
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					if (tileMap.getType(i, j).isTree()) {
						map.setTree(i, j, tileMap.getType(i, j).getTreeType((byte) 0), FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
					} else if (tileMap.getType(i, j).isBush()) {
						map.setBush(i, j, tileMap.getType(i, j).getBushType((byte) 0), FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			stopLoading();
			System.gc();//TODO
		}
	}

	private void parseAction(String action) {
		String[] parts = action.split(":");
		if (parts.length < 2)
			return;

		String[] options = parts[1].split(",");
		switch (parts[0]) {
		case "HEIGHTMAP":
			if (options.length != 9) {
				JOptionPane.showMessageDialog(null, "Not enough options for HEIGHTMAP", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				textField_mapSeed.setText(options[0]);
				comboBox_mapSize.setSelectedIndex(Integer.parseInt(options[1]));
				textField_mapResolution.setText(options[2]);
				textField_mapIterations.setText(options[3]);
				textField_mapMinEdge.setText(options[4]);
				textField_mapBorderWeight.setText(options[5]);
				textField_mapMaxHeight.setText(options[6]);
				textField_normalizeRatio.setText(options[7]);
				checkbox_moreLand.setSelected(Boolean.parseBoolean(options[8]));
				checkbox_mapRandomSeed.setSelected(false);
				textField_mapSeed.setEnabled(true);

				actionGenerateHeightmap();
			} catch (Exception nfe) {
				JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "ERODE":
			if (options.length != 3) {
				JOptionPane.showMessageDialog(null, "Not enough options for ERODE", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_erodeIterations.setText(options[0]);
			textField_erodeMinSlope.setText(options[1]);
			textField_erodeSediment.setText(options[2]);

			actionErodeHeightmap();
			break;
		case "DROPDIRT":
			if (options.length != 8) {
				JOptionPane.showMessageDialog(null, "Not enough options for DROPDIRT", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_biomeSeed.setText(options[0]);
			textField_waterHeight.setText(options[1]);
			textField_dirtPerTile.setText(options[2]);
			textField_maxDirtSlope.setText(options[3]);
			textField_maxDiagSlope.setText(options[4]);
			textField_maxDirtHeight.setText(options[5]);
			textField_cliffRatio.setText(options[6]);
			checkBox_landSlide.setSelected(Boolean.parseBoolean(options[7]));
			checkbox_biomeRandomSeed.setSelected(false);
			textField_biomeSeed.setEnabled(true);

			actionDropDirt();
			break;
		case "UPDATEWATER":
			if (options.length != 1) {
				JOptionPane.showMessageDialog(null, "Not enough options for DROPDIRT", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_waterHeight.setText(options[0]);

			actionUpdateWater();
			break;
		case "UNDOBIOME":
			actionUndoBiome();
			break;
		case "RESETBIOMES":
			actionResetBiomes();
			break;
		case "GENORES":
			if (options.length != 14) {
				JOptionPane.showMessageDialog(null, "Not enough options for GENORES", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_Rock.setText(options[0]);
			textField_Iron.setText(options[1]);
			textField_Gold.setText(options[2]);
			textField_Silver.setText(options[3]);
			textField_Zinc.setText(options[4]);
			textField_Copper.setText(options[5]);
			textField_Lead.setText(options[6]);
			textField_Tin.setText(options[7]);
			textField_Addy.setText(options[8]);
			textField_Glimmer.setText(options[9]);
			textField_Marble.setText(options[10]);
			textField_Slate.setText(options[11]);
			textField_Sandstone.setText(options[12]);
			textField_Rocksalt.setText(options[13]);

			actionGenerateOres();
			break;
		case "IMPORTHEIGHTMAP":
			if (options.length != 3) {
				JOptionPane.showMessageDialog(this, "Not enough options for HEIGHTMAP", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try{
				File heightImageFile = new File(actionsFileDirectory + "/" + options[0]);
				comboBox_mapSize.setSelectedIndex(Integer.parseInt(options[1]));
				textField_mapMaxHeight.setText(options[2]);

				api = null;
				genHistory = new ArrayList<String>();

				actionLoadHeightmap(heightImageFile);

				genHistory.add("IMPORTHEIGHTMAP:" + heightImageFile.getName() + 
						"," + comboBox_mapSize.getSelectedIndex() + "," + textField_mapMaxHeight.getText());


			} catch (Exception nfe) {
				JOptionPane.showMessageDialog(this, "Error: " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
			}
			break;

		default:
			if(parts[0].startsWith("SEEDBIOME")) {
				if (options.length != 17) {
					JOptionPane.showMessageDialog(null, "Not enough options for SEEDBIOME", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					int i = 0;
					comboBox_biomeType.setSelectedIndex(Integer.parseInt(options[i++]));
					textField_seedCount.setText(options[i++]);
					textField_biomeSize.setText(options[i++]);
					textField_biomeDensity.setText(options[i++]);
					textField_biomeMaxSlope.setText(options[i++]);
					textField_growthN.setText(options[i++]);
					textField_growthS.setText(options[i++]);
					textField_growthE.setText(options[i++]);
					textField_growthW.setText(options[i++]);
					checkbox_growthRandom.setSelected(Boolean.parseBoolean(options[i++]));
					textField_growthMin.setText(options[i++]);
					textField_growthMax.setText(options[i++]);
					textField_biomeMinHeight.setText(options[i++]);
					textField_biomeMaxHeight.setText(options[i++]);
					chckbxAroundWater.setSelected(Boolean.parseBoolean(options[i++]));
					comboBox_FlowerType.setSelectedIndex(Integer.parseInt(options[i++]));
					textField_FlowerPercent.setText(options[i++]);

					actionSeedBiome(null);
				} catch (Exception nfe) {
					JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				}
			} else if(parts[0].startsWith("PAINTBIOME")) {
				if (options.length != 18) {
					JOptionPane.showMessageDialog(null, "Not enough options for SEEDBIOME", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					int i = 0;
					comboBox_biomeType.setSelectedIndex(Integer.parseInt(options[i++]));
					Point origin = new Point(Integer.parseInt(options[i++]),Integer.parseInt(options[i++]));
					textField_biomeSize.setText(options[i++]);
					textField_biomeDensity.setText(options[i++]);
					textField_biomeMaxSlope.setText(options[i++]);
					textField_growthN.setText(options[i++]);
					textField_growthS.setText(options[i++]);
					textField_growthE.setText(options[i++]);
					textField_growthW.setText(options[i++]);
					checkbox_growthRandom.setSelected(Boolean.parseBoolean(options[i++]));
					textField_growthMin.setText(options[i++]);
					textField_growthMax.setText(options[i++]);
					textField_biomeMinHeight.setText(options[i++]);
					textField_biomeMaxHeight.setText(options[i++]);
					chckbxAroundWater.setSelected(Boolean.parseBoolean(options[i++]));
					comboBox_FlowerType.setSelectedIndex(Integer.parseInt(options[i++]));
					textField_FlowerPercent.setText(options[i++]);

					actionSeedBiome(origin);
				} catch (Exception nfe) {
					JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				System.err.println("Error importing, Unknown action: "+options[0]);
			}
			break;
		}
	}

	private class ActionFileView extends FileFilter {

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null)
				if (extension.equals("act"))
					return true;

			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

		@Override
		public String getDescription() {
			return "Action Files (.act)";
		}
	}

	private class TextFileView extends FileFilter {

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null)
				if (extension.equals("txt"))
					return true;

			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

		@Override
		public String getDescription() {
			return "Biome Files (.txt)";
		}
	}

	private class ImageFileView extends FileFilter {

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null)
				if (extension.equals("png"))
					return true;

			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

		@Override
		public String getDescription() {
			return "Image File (.png)";
		}
	}

	private void setRockTotal() {
		try {
			double[] rates = { Double.parseDouble(textField_Iron.getText()), Double.parseDouble(textField_Gold.getText()),
					Double.parseDouble(textField_Silver.getText()), Double.parseDouble(textField_Zinc.getText()), Double.parseDouble(textField_Copper.getText()),
					Double.parseDouble(textField_Lead.getText()), Double.parseDouble(textField_Tin.getText()), Double.parseDouble(textField_Addy.getText()),
					Double.parseDouble(textField_Glimmer.getText()), Double.parseDouble(textField_Marble.getText()), Double.parseDouble(textField_Slate.getText()), 
					Double.parseDouble(textField_Sandstone.getText()), Double.parseDouble(textField_Rocksalt.getText())
			};

			float total = 0;
			for (int i = 0; i < rates.length; i++)
				total += rates[i];

			textField_Rock.setText(""+(100.0f - total));
		} catch (NumberFormatException nfe) {

		}
	}

	private BufferedImage getBiomeImage() {
		int mapSize = heightMap.getMapSize();
		BufferedImage bufferedImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
		WritableRaster wr = (WritableRaster) bufferedImage.getRaster();

		int[] array = new int[mapSize * mapSize * 3];
		for (int x = 0; x < mapSize; x++) {
			for (int y = 0; y < mapSize; y++) {
				final Tile tile = api.getMapData().getSurfaceTile(x, y);
				final Color color;
				if (tile != null) {
					if (tile == Tile.TILE_GRASS && tileMap.getFlowerType(x, y) != 0) {
						color = new Color(220,250,tileMap.getFlowerType(x, y)+50);
					} else {
						color = TileMap.getTileColor(tile);
					}
				}
				else {
					color = TileMap.getTileColor(Tile.TILE_DIRT);
				}
				array[(x + y * mapSize)*3+0] = color.getRed();
				array[(x + y * mapSize)*3+1] = color.getGreen();
				array[(x + y * mapSize)*3+2] = color.getBlue();
			}
		}

		wr.setPixels(0, 0, mapSize, mapSize, array);

		bufferedImage.setData(wr);
		return bufferedImage;
	}

	public void updateMapCoords (int x, int y, boolean show) {
		if (show && tileMap != null) {
			int height = tileMap.getMapHeight(x, mapPanel.getMapSize()-y);
			lblMapCoords.setText("Tile ("+x+","+y+"), Player ("+(x*4)+","+(y*4)+"), Height ("+height+")");
		} else {
			lblMapCoords.setText("Right click to place a marker");
		}
	}

	public void submitError(String err) {
		textArea_Errors.append(err);
		btnViewErrors.setVisible(true);
	}

	public static void log (String s) { 
		System.out.println(s);
	}
}