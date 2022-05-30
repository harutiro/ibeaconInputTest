package io.github.com.harutiro.ibeaconinputtest

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class MainActivity : AppCompatActivity() {

    //tagName
    private val TAG: String = "HogeActivity"
    // iBeaconのデータを認識するためのParserフォーマット
    val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    //パーミッション確認ようのコード
    private val PERMISSION_REQUEST_COARSE_LOCATION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //パーミッション確認
        //TODO:ロケーションの取得の常に許可をできるようにする
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        //絞り込みをする部分
        //今回nullなので、全てを取得する。
        //id1:uuid id2:major id3:minor
        val mRegion = Region("unique-id-001", null, null, null)

        val beaconManager =  BeaconManager.getInstanceForApplication(this)
        // Set up a Live Data observer so this Activity can get ranging callbacks
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        beaconManager.getRegionViewModel(mRegion).rangedBeacons.observe(this, rangingObserver)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_FORMAT))
        beaconManager.startRangingBeacons(mRegion)
    }

    //取得した時の動作部分
    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
        }
    }
}