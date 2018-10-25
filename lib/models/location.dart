part of sy_flutter_amap;

class SyLocation {
  num lon;
  num lat;
  String country;
  String province;
  String city;
  String district;

  SyLocation(
      {this.lon,
      this.lat,
      this.country,
      this.province,
      this.city,
      this.district});

  factory SyLocation.fromJson(Map<String, dynamic> json) => SyLocation(
        lon: json['lon'] ?? 0.0,
        lat: json['lat'] ?? 0.0,
        country: json['country'] ?? '',
        province: json['province'] ?? '',
        city: json['city'] ?? '',
        district: json['district'] ?? '',
      );

  Map<String, dynamic> toJon() {
    return {
      "lon": lon,
      "lat": lat,
      "country": country,
      "province": province,
      "city": city,
      "district": district
    };
  }
}
