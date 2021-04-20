

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.sql.PreparedStatement;

public class Main {

    static String url;
    static Random rand;

    static int id = 0;

    public static void main(String[] args) {

        url = "jdbc:sqlite:C:\\SQLite\\card.db";


        system();
    }


    public static void system () {

        rand = new Random(999999999);
        Scanner scanner = new Scanner(System.in);
        boolean exit = true;


        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER  PRIMARY KEY," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER )");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        while (exit){

            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            System.out.println();


            switch (scanner.nextInt()){

                case 1:
                    createAnAccount();
                    break;

                case 2:
                    exit = logInAccount();
                    break;

                case 0:
                    System.out.print("Bye!");
                    exit = false;
                    break;
            }

        }
    }

    public static void createAnAccount() {

        id ++;

        String card = "400000" + (rand.nextInt(999999999 -100000000 + 1 ) +100000000);
        String pin;
        char[] nums = card.toCharArray();

        int sum = 0;
        int y;
        int lastnum = 0;

        for (int i = 0; i < nums.length ; i++) {

            y =  nums[i] - '0';

            if (i % 2 == 0){
                y = y*2;
            }

            if (y > 9){
                y = y-9;
            }
            sum += y;
        }

        while (true){
            if ((sum + lastnum) % 10 == 0){
                break;
            }
            lastnum++;
        }

        card = card + lastnum;
        pin = "" + (rand.nextInt(9999 - 1000 + 1)+1000);

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(card);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        System.out.println();

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                int i = statement.executeUpdate("INSERT INTO card VALUES" +
                        "("+ id + ", '" + card + "', " + "'" + pin +"', " + 0 + ");");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean logInAccount() {
        Scanner scanner = new Scanner(System.in);
        boolean inAccount = false;


        System.out.println("Enter your card number:");
        String log = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String pass = scanner.nextLine();



        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution

                try (ResultSet cards = statement.executeQuery("SELECT * FROM card")) {
                    while (cards.next()){
                        if (log.equals(cards.getString("number")) && pass.equals(cards.getString("pin")) ){
                            System.out.println("You have successfully logged in!");
                            System.out.println();
                            inAccount = true;
                            //return account(log);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (inAccount){
            return account(log);          }

        System.out.println("Wrong card number or PIN!");
        System.out.println();

        return true;
    }

    public static boolean account(String cardNumber){

        boolean y = true;

        Scanner scanner = new Scanner(System.in);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);


        while (y){
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            System.out.println();

            switch(scanner.nextInt()){

                // balance
                case 1:
                    try (Connection con = dataSource.getConnection()) {
                        // Statement creation
                        try (Statement statement = con.createStatement()) {
                            // Statement execution

                            try (ResultSet cards = statement.executeQuery("SELECT * FROM card WHERE number = '"+ cardNumber + "'")) {

                                System.out.println("Balance: " + cards.getInt("balance"));

                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                // add income
                case 2:

                    System.out.println("Enter income:");

                    try (Connection con = dataSource.getConnection()) {
                        // Statement creation
                        String updateBalance = "UPDATE card SET balance = balance + ?  WHERE number = ?";

                        try (PreparedStatement preparedStatement = con.prepareStatement(updateBalance)) {
                            preparedStatement.setInt(1,scanner.nextInt());
                            preparedStatement.setString(2,cardNumber);

                            preparedStatement.executeUpdate();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Income was added!");
                    System.out.println();

                    break;

                // do transfer
                case 3:

                    System.out.println("Transfer");
                    System.out.println("Enter card number:\n");

                    String num = scanner.next();
                    char[] nums = num.toCharArray();
                    int sum = 0;
                    int x;
                    boolean isExist = false;


                    for (int i = 0 ; i < nums.length ; i++){

                        x = nums[i] - '0';

                        if (i % 2 ==0){
                            x = x*2;
                        }
                        if (x > 9 ){
                            x = x- 9;
                        }
                        sum += x;
                    }
                    //sum += nums[15];

                    if (sum % 10 != 0){
                        System.out.println("Probably you made a mistake in the card number. Please try again!\n");
                        break;
                    }

                    try (Connection con = dataSource.getConnection()) {
                        // Statement creation
                        try (Statement statement = con.createStatement()) {
                            // Statement execution

                            try (ResultSet cards = statement.executeQuery("SELECT * FROM card")) {
                                while (cards.next()){
                                    if (num.equals(cards.getString("number")) ){
                                        isExist = true;
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (!isExist){
                        System.out.println("Such a card does not exist.\n");
                        break;
                    }
                    if (num.equals(cardNumber)){
                        System.out.println("You can't transfer money to the same account!\n");
                        break;
                    }
                    System.out.println("Enter how much money you want to transfer:\n");

                    int value = scanner.nextInt();

                    try (Connection con = dataSource.getConnection()) {
                        // Statement creation
                        try (Statement statement = con.createStatement()) {
                            // Statement execution

                            try (ResultSet cards = statement.executeQuery("SELECT * FROM card WHERE number = " + cardNumber)) {

                                    if (value > cards.getInt("balance") ){
                                        System.out.println("Not enough money!\n");
                                        break;
                                    }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try (Connection con = dataSource.getConnection()) {
                        // Statement creation
                        String balancePlus = "UPDATE card SET balance = balance + ?  WHERE number = ?";

                        try (PreparedStatement preparedStatement = con.prepareStatement(balancePlus)) {
                            preparedStatement.setInt(1,value);
                            preparedStatement.setString(2,num);

                            preparedStatement.executeUpdate();
                        }
                        String balanceMinus = "UPDATE card SET balance = balance - ?  WHERE number = ?";

                        try (PreparedStatement preparedStatement = con.prepareStatement(balanceMinus)) {
                            preparedStatement.setInt(1,value);
                            preparedStatement.setString(2,cardNumber);

                            preparedStatement.executeUpdate();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Success!");
                    break;

                // close account
                case 4:
                    System.out.println("The account has been closed!");

                    try (Connection con = dataSource.getConnection()) {
                        // Statement creation
                        try (Statement statement = con.createStatement()) {
                            // Statement execution
                            statement.executeUpdate("DELETE FROM card WHERE number = " + cardNumber);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return true;


                case 5:
                    System.out.println("You have successfully logged out!");
                    y = false;
                    return true;

                case 0:
                    System.out.println("Bye!");
                    y = false;
                    return false;

            }
        }
        return true;
    }
}
