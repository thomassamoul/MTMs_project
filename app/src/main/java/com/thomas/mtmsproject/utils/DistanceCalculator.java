package com.thomas.mtmsproject.utils;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.thomas.mtmsproject.constant.Constant;
import com.thomas.mtmsproject.models.Drivers;

import java.util.List;

public class DistanceCalculator {

    float minDistance = 0;

    public void findNearbyLocation(Context context, List<Drivers> list) {
        Location currentLocation = new Location("");
        Drivers driversModel = new Drivers();
        currentLocation.setLatitude(Constant.currentLocation.latitude);
        currentLocation.setLongitude(Constant.currentLocation.longitude);
        for (Drivers drivers : list) {
            Location driverLocation = new Location("");

            driverLocation.setLatitude(drivers.getLat());
            driverLocation.setLongitude(drivers.getLng());

            if (minDistance == 0) {
                minDistance = driverLocation.distanceTo(currentLocation);
                driversModel = drivers;
            } else if (driverLocation.distanceTo(currentLocation) < minDistance) {
                minDistance = driverLocation.distanceTo(currentLocation);
                driversModel = drivers;
            }
        }
        Toast.makeText(context, driversModel.getName(), Toast.LENGTH_SHORT).show();

    }

}