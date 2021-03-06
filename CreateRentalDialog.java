import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.List;
public class CreateRentalDialog extends JDialog
                                 implements ActionListener
{
   private JRadioButton      gamesButton;
   private JRadioButton      moviesButton;
   private JButton           submitButton;

   private ButtonGroup       gamesOrMoviesButtonGroup;

   private JPanel            topPanel;
   private JPanel            buttonPanel;
   
   private JTable            table;
   private Connection        connection;
   private DBHandler         dbhandler;
   private ResultSet         resultSet;
   private ResultSetMetaData metaData;
   private PreparedStatement pstmt;
   private Statement         statement;

   
   private JLabel            titleLabel;
   private JLabel            amountLabel;
   private JLabel            releaseDateLabel;
   private JLabel            genreLabel;
   private JLabel            platformLabel;
   private JLabel            castMemberLabel;
   private JLabel            directorLabel;
   private JLabel            sequelLabel;
   private JLabel            awardsLabel;


   private JTextField        titleField;
   private JTextField        amountField;
   private JTextField        genreField;
   private JTextField        platFormField;
   private JTextField        directorField;
   private JTextField        castMemberField;
   private JTextField        prequelField;
   private JTextField        awardsField;


   private JSpinner          dateSpinner;
   private SpinnerDateModel  datemodel;
   private JComponent        spinEditor;

   public CreateRentalDialog(Connection newConnection)
   {
      connection               = newConnection;
      topPanel                 = new JPanel();
      buttonPanel              = new JPanel();
      dbhandler                = new DBHandler();

      datemodel                = new SpinnerDateModel();
      dateSpinner              = new JSpinner(datemodel);
      spinEditor               = new JSpinner.DateEditor(dateSpinner,"yyyy-MM-dd");
      
      dateSpinner.setEditor(spinEditor);

      gamesButton              = new JRadioButton("games");
      moviesButton             = new JRadioButton("movies",true);
      
      gamesOrMoviesButtonGroup = new ButtonGroup();
      gamesOrMoviesButtonGroup.add(moviesButton);
      gamesOrMoviesButtonGroup.add(gamesButton);
      
      submitButton             = new JButton("Submit");

      submitButton.setActionCommand("SUBMIT");
      submitButton.addActionListener(this);


      buttonPanel.add(submitButton);
      buttonPanel.add(gamesButton);
      buttonPanel.add(moviesButton);
      buttonPanel.setBorder(BorderFactory.createTitledBorder("Rental type:"));

      moviesButton.setActionCommand("MOVIES");
      gamesButton.setActionCommand("GAMES");
      moviesButton.addActionListener(this);
      gamesButton.addActionListener(this);

      getRootPane().setDefaultButton(submitButton);
      
      
      titleLabel       = new JLabel("Title(*):");
      amountLabel      = new JLabel("Number of Available Copy's(*):");
      releaseDateLabel = new JLabel("ReleaseDate(yyyy-mm-dd):");
      genreLabel       = new JLabel("Genre(*):");
      platformLabel    = new JLabel("Platform(*):");
      castMemberLabel  = new JLabel("CastMember(PID)(*)(comma separators):");
      directorLabel    = new JLabel("Director(PID)(*):");
      sequelLabel      = new JLabel("Sequal(RID):");
      awardsLabel      = new JLabel("Awards Won(comma separators):");
      
      titleField       = new JTextField();
      amountField      = new JTextField();
      genreField       = new JTextField();
      platFormField    = new JTextField();
      directorField    = new JTextField();
      castMemberField  = new JTextField();
      prequelField     = new JTextField();
      awardsField      = new JTextField();

      topPanel.setLayout(new GridLayout(18,2,0,5));
      
      topPanel.add(titleLabel);
      topPanel.add(titleField);
      
      topPanel.add(amountLabel);
      topPanel.add(amountField);
      
      topPanel.add(releaseDateLabel);
      topPanel.add(dateSpinner);

      topPanel.add(genreLabel);
      topPanel.add(genreField);

      topPanel.add(platformLabel);
      topPanel.add(platFormField);
      
      topPanel.add(directorLabel);
      topPanel.add(directorField);
      
      topPanel.add(castMemberLabel);
      topPanel.add(castMemberField);

      topPanel.add(sequelLabel);
      topPanel.add(prequelField);

      topPanel.add(awardsLabel);
      topPanel.add(awardsField);

      add(topPanel,BorderLayout.CENTER);      
      
      add(buttonPanel,BorderLayout.SOUTH);

      getRootPane().setDefaultButton(submitButton);
      this.setupMainFrame();
      fieldUpdater();
      
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void setupMainFrame()
  {
    Toolkit   tk = Toolkit.getDefaultToolkit();
    Dimension d  = tk.getScreenSize();
    this.setSize(500,750);
    this.setMinimumSize(new Dimension(500,750));
    this.setLocation(d.width/4, d.height/8);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setTitle("Add Rental");
    setVisible(true);
  }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public void actionPerformed(ActionEvent e)
{  

  if((e.getActionCommand().equals("MOVIES"))||(e.getActionCommand().equals("GAMES")))
  {
    fieldUpdater();
  }
  else if(e.getActionCommand().equals("SUBMIT"))
  {
    if (gamesButton.isSelected() && !(titleField.getText().trim().equals("")) && !(amountField.getText().trim().equals("")) && !(genreField.getText().trim().equals("")) && !(platFormField.getText().trim().equals("")))
    {
      try
      {
        Integer.parseInt(amountField.getText().trim());
        createRentalQueryExecuter();
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog(this,"Please make sure data in either the # of available copy's field is an integer!","RIP.",JOptionPane.WARNING_MESSAGE);
      }
    }
    else if(moviesButton.isSelected() && !(titleField.getText().trim().equals("")) && !(amountField.getText().trim().equals(""))  && !(genreField.getText().trim().equals("")) && !(directorField.getText().trim().equals("") && !(castMemberField.getText().trim().equals(""))))
    {
      try
      {
        Integer.parseInt(amountField.getText().trim());
        createRentalQueryExecuter();
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog(this,"Please make sure data in either the # of available copy's field is an integer!","RIP.",JOptionPane.WARNING_MESSAGE);
      }
    }
    else
    {
      JOptionPane.showMessageDialog(this,"Please make sure data is entered in the required fields, see (*)!","RIP.",JOptionPane.WARNING_MESSAGE);
    }
      //Values in editInfo should be ready to send in a transaction to update userInfo
  }
}//end of action performed

void fieldUpdater()
{
    if(moviesButton.isSelected())
    {
      castMemberField.setEnabled(true);
      directorField.setEnabled(true);
      prequelField.setEnabled(true);
      awardsField.setEnabled(true);
      platFormField.setEnabled(false);
    }
    else if (gamesButton.isSelected())
    {
      castMemberField.setEnabled(false);
      directorField.setEnabled(false);
      prequelField.setEnabled(false);
      awardsField.setEnabled(false);
      platFormField.setEnabled(true);
    }
}     
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void createRentalQueryExecuter()
{
  int                  maxRID = 0;
  String               castORAwardString;
  String[]             castORAwardList;
  try 
  {
//-------------------------------------------------------------------------------------------------------------------------------
    // Retrieves the max rid from the rental table, then sets our max rid to it + 1 for use later on. 
    statement = connection.createStatement();
    resultSet = statement.executeQuery("SELECT MAX(RID) FROM Rentals");
    System.out.println("statement: " + statement.toString());
    if(!resultSet.next()) 
    {
      JOptionPane.showMessageDialog(null,"No records found!");
      return;
    }
    else
      maxRID = resultSet.getInt(1) + 1;
    statement.close();
//-------------------------------------------------------------------------------------------------------------------------------
    //This section will insert into the rentals table, a rental with a new RID found from the max rid
    // the rest of the data will be taken from the text fields and inserted as well.
    pstmt = connection.prepareStatement("INSERT rentals (rid, title, releaseDate, num_availible_copys) VALUES (?, ?, ?, ?);");
    pstmt.setInt(1,maxRID);
    pstmt.setString(2, titleField.getText().trim());//pname
    pstmt.setDate(3, new java.sql.Date(datemodel.getDate().getTime()));
    pstmt.setInt(4, Integer.parseInt(amountField.getText().trim()));// sets amountField  of available copys int
    System.out.println("titleField "+ titleField.getText().trim());
    System.out.println("pstmt: " + pstmt.toString());
    System.out.println("About to Execute RENTAL INSERT");
    pstmt.execute();
//-------------------------------------------------------------------------------------------------------------------------------
    //This section will insert into the belongs_to_genre table, it will contain the user specified genre and RID from the max rid. 
    pstmt.clearParameters();
    pstmt = connection.prepareStatement("INSERT belongs_to_genre(gName, rid)  VALUES(?, ?);");
    pstmt.setString(1, genreField.getText().trim());//Sets the Genre name from the text field
    pstmt.setInt(2,maxRID);
    System.out.println("pstmt: " + pstmt.toString());
    System.out.println("About to Execute GENRE INSERT");
    pstmt.execute();
//-------------------------------------------------------------------------------------------------------------------------------
    // here we check to see if the specified rental is a movie or a game, if its a game we will disable movie specific textfields, game specific textfields will be disabled if its a movie.
    if(moviesButton.isSelected())
    {
       //This section will make an insert into the movie table, using the provided values from the textfields. 
        pstmt.clearParameters();
        pstmt = connection.prepareStatement("INSERT movie (rid, pid, rid_of_prequel) VALUES (?, ?, ?);");
        pstmt.setInt(1,maxRID);
        pstmt.setInt(2, Integer.parseInt(directorField.getText().trim()));//SETS DIRECTOR FROM DIRECTORS PID, maybe NEED TO FIX TO MAKE IT ACTUALLY LOOK UP THE DIRECTOR NAME?????????

        if(!prequelField.getText().trim().equals(""))//checks for data in the prequel field
          pstmt.setInt(3,Integer.parseInt(prequelField.getText().trim()));//SETS PREQUEL FROM prequelField RID,  maybe NEED TO FIX TO MAKE IT ACTUALLY LOOK UP THE MOVIE?????????
        else
          pstmt.setNull(3,Types.INTEGER);//SETS PREQUEL FROM prequelField RID to null if left blank!

        System.out.println("pstmt: " + pstmt.toString());
        System.out.println("About to Execute INSERT movie");
        pstmt.execute();
//-------------------------------------------------------------------------------------------------------------------------------
       // AND HERES WHERE  I WOULD PUT MY CAST MEMBERS, IF I HAD ANY!!!!!!!!!!!!!!!!!!!!!!
       // Just kidding we have cast members now, they can be added or deleted at will for the specified movie. 
        // currently only allows one cast member insert at a time.

          castORAwardString   = castMemberField.getText().trim();
          castORAwardList     = castORAwardString.split(",");
          for(int i = 0; i < castORAwardList.length; i++)
          {
            pstmt.clearParameters();
            pstmt = connection.prepareStatement("INSERT was_in(pid,rid) VALUES (?, ?);");
            pstmt.setInt(1, Integer.parseInt(castORAwardList[i]));
            pstmt.setInt(2,maxRID);
            System.out.println("pstmt: " + pstmt.toString());
            System.out.println("About to INSERT INTO  was_in");
            pstmt.execute();
          }
//-------------------------------------------------------------------------------------------------------------------------------
        //checks to see if the awards text field is blank, if it is we will not run this query,
        // otherwise this query will insert the specified award title into the has_won_award table,
        // currently only allows one award insertion at a time.
        if(!awardsField.getText().trim().equals(""))// checks to see if award textfield is blank
        {
          castORAwardString   = awardsField.getText().trim();
          castORAwardList     = castORAwardString.split(",");
          for(int i = 0; i < castORAwardList.length; i++)
          {
            pstmt.clearParameters();
            pstmt = connection.prepareStatement("INSERT has_won_award(rid, aTitle) VALUES (?, ?);");
            pstmt.setInt(1, maxRID);// Sets pid for new person value
            pstmt.setString(2, castORAwardList[i]);
            System.out.println("pstmt: " + pstmt.toString());
            System.out.println("About to Execute INSERT has_won_award");
            pstmt.execute();
          }
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------
    else if(gamesButton.isSelected())
    {    
       //This section will make an insert into the game table, it will simply insert the specified rid from the data found earlier. 
        pstmt.clearParameters();
        pstmt = connection.prepareStatement("INSERT game (rid) VALUE (?);");
        pstmt.setInt(1,maxRID);
        System.out.println("pstmt: " + pstmt.toString());
        System.out.println("About to  INSERT game");
        pstmt.execute();
//-------------------------------------------------------------------------------------------------------------------------------
      // This Section will insert the specified platform the game runs on  into the plays_on_platform table
      // currently only allows one platform insert at a time.   
        pstmt.clearParameters();
        pstmt = connection.prepareStatement("INSERT plays_on_platform (rid, platName) VALUE (?, ?);");
        pstmt.setInt(1,maxRID);
        pstmt.setString(2, platFormField.getText().trim());
        System.out.println("pstmt: " + pstmt.toString());
        System.out.println("About to  INSERT INTO plays_on_platform");
        pstmt.execute();
    }
    pstmt.close();
    System.out.println("LEAVING createRentalQueryExecuter");
    JOptionPane.showMessageDialog(null, "Your rental has been entered into the database, please refresh your table to see results!", "Well thats pretty neat!", JOptionPane.INFORMATION_MESSAGE);
  }//end of try
  catch(SQLException ex) 
  {
    System.out.println(ex.getMessage());
    JOptionPane.showMessageDialog(null, ex.getMessage(), "Query error!", JOptionPane.ERROR_MESSAGE);
  }
  catch (NumberFormatException nfe)
  {
    JOptionPane.showMessageDialog(this,"Please make sure data in either the zip field or the quotaField is an integer!","RIP.",JOptionPane.WARNING_MESSAGE);
  }
}// END OF createRentalQueryExecuter
}//END OF CLASS