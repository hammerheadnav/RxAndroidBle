package com.polidea.rxandroidble.internal.util

import android.os.Build
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class LocationServicesStatusTest extends Specification {

    def mockCheckerLocationProvider = Mock CheckerLocationProvider
    def mockCheckerLocationPermission = Mock CheckerLocationPermission
    int mockDeviceSdk
    int mockApplicationTargetSdk
    boolean mockIsAndroidWear
    LocationServicesStatus objectUnderTest

    private prepareObjectUnderTest() {
        objectUnderTest = new LocationServicesStatus(mockCheckerLocationProvider, mockCheckerLocationPermission, mockDeviceSdk, mockApplicationTargetSdk, mockIsAndroidWear)
    }

    @Shared
    private def sdkVersionsPreM = [
            Build.VERSION_CODES.JELLY_BEAN_MR2,
            Build.VERSION_CODES.KITKAT,
            Build.VERSION_CODES.KITKAT_WATCH,
            Build.VERSION_CODES.LOLLIPOP,
            Build.VERSION_CODES.LOLLIPOP_MR1,
    ]

    @Shared
    private def sdkVersionsPostM = [
            Build.VERSION_CODES.M,
            Build.VERSION_CODES.N,
            Build.VERSION_CODES.CUR_DEVELOPMENT,
    ]

    @Shared
    private def sdkVersions = sdkVersionsPreM + sdkVersionsPostM

    @Shared
    private def isAndroidWear = [true, false]

    @Unroll
    def "(SDK <23) isLocationPermissionOk should return true (SDK=#sdkVersion)"() {

        given:
        mockDeviceSdk = sdkVersion
        prepareObjectUnderTest()

        expect:
        objectUnderTest.isLocationPermissionOk()

        where:
        sdkVersion << sdkVersionsPreM
    }

    @Unroll
    def "(SDK <23) isLocationPermissionOk should not call CheckerLocationPermission (SDK=#sdkVersion)"() {

        given:
        mockDeviceSdk = sdkVersion
        prepareObjectUnderTest()

        when:
        objectUnderTest.isLocationPermissionOk()

        then:
        0 * mockCheckerLocationPermission.isLocationPermissionGranted()

        where:
        sdkVersion << sdkVersionsPreM
    }

    @Unroll
    def "(SDK >=23) isLocationPermissionOk should always return true (permissionGranted:#permissionGranted SDK:#sdkVersion)"() {

        given:
        mockDeviceSdk = sdkVersion
        prepareObjectUnderTest()

        expect:
        objectUnderTest.isLocationPermissionOk() == true

        where:
        [sdkVersion, permissionGranted] << [sdkVersionsPostM, [true, false]].combinations()
    }

    @Unroll
    def "should not check location provider(deviceSdk:#sdkVersion targetSdk:#targetSdk isAndroidWear:#isAndroidWearValue)"() {

        given:
        mockDeviceSdk = sdkVersion
        mockApplicationTargetSdk = targetSdk
        mockIsAndroidWear = isAndroidWearValue
        prepareObjectUnderTest()

        when:
        objectUnderTest.isLocationProviderOk()

        then:
        0 * mockCheckerLocationProvider.isLocationProviderEnabled() >> true

        where:
        [sdkVersion, targetSdk, isAndroidWearValue] << [sdkVersions, sdkVersions, isAndroidWear].combinations()
    }
}
