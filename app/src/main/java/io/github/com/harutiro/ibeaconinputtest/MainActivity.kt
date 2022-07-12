package io.github.com.harutiro.ibeaconinputtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import io.github.com.harutiro.ibeaconinputtest.databinding.ActivityMainBinding
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {

    //bindingの部分
    private lateinit var binding: ActivityMainBinding

    //tagName
    private val TAG: String = "HogeActivity"
    // iBeaconのデータを認識するためのParserフォーマット
    val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    //パーミッション確認用のコード
    private val PERMISSION_REQUEST_CODE = 1
    //出力結果を保存する場所
    var outputText = ""

    //どのパーミッションを許可したいかリスト化する
    val permissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    }else{
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //パーミッション確認
        //TODO:ロケーションの取得の常に許可をできるようにする
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            // パーミッションが許可されていない時の処理
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", PERMISSION_REQUEST_CODE, *permissions)
        }

        //パーミッションが許可された時にIbeaconが動く
        if(EasyPermissions.hasPermissions(this, *permissions)){
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


    }

    //取得した時の動作部分
    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        outputText += "Ranged: ${beacons.count()} beacons\n"
        binding.textView.text = outputText

        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
            outputText += "$beacon about ${beacon.distance} meters away\n"
            binding.textView.text = outputText
        }
    }
}