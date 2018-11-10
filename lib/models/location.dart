part of sy_flutter_amap;

//官方文档 https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
class SyLocation {
  num longitude;
  num latitude;
  String country;
  String province;
  String city;
  String district;

  String road;
  String street;
  String number;
  String address;

  SyLocation({
    this.longitude,
    this.latitude,
    this.country,
    this.province,
    this.city,
    this.district,
    this.road,
    this.street,
    this.number,
    this.address,
  });

  factory SyLocation.fromJson(Map<String, dynamic> json) => SyLocation(
        longitude: json['lon'] ?? 0.0,
        latitude: json['lat'] ?? 0.0,
        country: json['country'] ?? '',
        province: json['province'] ?? '',
        city: json['city'] ?? '',
        district: json['district'] ?? '',
        road: json['road'] ?? '',
        street: json['street'] ?? '',
        number: json['number'] ?? '',
        address: json['address'] ?? '',
      );

  Map<String, dynamic> toJon() {
    return {
      'longitude': longitude,
      'latitude': latitude,
      'country': country,
      'province': province,
      'city': city,
      'district': district,
      'road': road,
      'street': street,
      'number': number,
      'address': address,
    };
  }
}
