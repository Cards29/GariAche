package com.example.gariache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GariAche.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RIDES = "rides";
    public static final String TABLE_BOOKINGS = "bookings";

    // Common Columns
    public static final String COLUMN_ID = "id";

    // Users Columns
    public static final String COLUMN_USER_STUDENT_ID = "student_id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Rides Columns
    public static final String COLUMN_RIDE_DRIVER_ID = "driver_id";
    public static final String COLUMN_RIDE_PICKUP = "pickup_point";
    public static final String COLUMN_RIDE_DESTINATION = "destination";
    public static final String COLUMN_RIDE_DATE = "ride_date";
    public static final String COLUMN_RIDE_TIME = "ride_time";
    public static final String COLUMN_RIDE_SEATS = "available_seats";
    public static final String COLUMN_RIDE_PRICE = "price_per_seat";
    public static final String COLUMN_RIDE_CAR_MODEL = "car_model";
    public static final String COLUMN_RIDE_CAR_COLOR = "car_color";
    public static final String COLUMN_RIDE_LICENSE_PLATE = "license_plate";
    public static final String COLUMN_RIDE_NOTES = "notes";
    public static final String COLUMN_RIDE_STATUS = "status";

    // Bookings Columns
    public static final String COLUMN_BOOKING_RIDE_ID = "ride_id";
    public static final String COLUMN_BOOKING_PASSENGER_ID = "passenger_id";
    public static final String COLUMN_BOOKING_SEATS = "booked_seats";
    public static final String COLUMN_BOOKING_PHONE = "passenger_phone";
    public static final String COLUMN_BOOKING_NOTES = "notes";
    public static final String COLUMN_BOOKING_DATE = "booking_date";
    public static final String COLUMN_BOOKING_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_STUDENT_ID + " TEXT UNIQUE NOT NULL,"
                + COLUMN_USER_NAME + " TEXT NOT NULL,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COLUMN_USER_PHONE + " TEXT,"
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL"
                + ")";

        // Create Rides Table
        String CREATE_RIDES_TABLE = "CREATE TABLE " + TABLE_RIDES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RIDE_DRIVER_ID + " INTEGER,"
                + COLUMN_RIDE_PICKUP + " TEXT DEFAULT 'IUT',"
                + COLUMN_RIDE_DESTINATION + " TEXT NOT NULL,"
                + COLUMN_RIDE_DATE + " TEXT NOT NULL,"
                + COLUMN_RIDE_TIME + " TEXT NOT NULL,"
                + COLUMN_RIDE_SEATS + " INTEGER NOT NULL,"
                + COLUMN_RIDE_PRICE + " INTEGER DEFAULT 0,"
                + COLUMN_RIDE_CAR_MODEL + " TEXT,"
                + COLUMN_RIDE_CAR_COLOR + " TEXT,"
                + COLUMN_RIDE_LICENSE_PLATE + " TEXT,"
                + COLUMN_RIDE_NOTES + " TEXT,"
                + COLUMN_RIDE_STATUS + " TEXT DEFAULT 'active',"
                + "FOREIGN KEY(" + COLUMN_RIDE_DRIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";

        // Create Bookings Table
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BOOKING_RIDE_ID + " INTEGER,"
                + COLUMN_BOOKING_PASSENGER_ID + " INTEGER,"
                + COLUMN_BOOKING_SEATS + " INTEGER NOT NULL,"
                + COLUMN_BOOKING_PHONE + " TEXT NOT NULL,"
                + COLUMN_BOOKING_NOTES + " TEXT,"
                + COLUMN_BOOKING_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_BOOKING_STATUS + " TEXT DEFAULT 'confirmed',"
                + "FOREIGN KEY(" + COLUMN_BOOKING_RIDE_ID + ") REFERENCES " + TABLE_RIDES + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_BOOKING_PASSENGER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";

        // Execute the queries
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_RIDES_TABLE);
        db.execSQL(CREATE_BOOKINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }
}
