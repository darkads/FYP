<script type="text/javascript" 
  src="http://maps.google.com/maps/api/js?sensor=true"></script>
<script type="text/javascript">
  var map;
  function initialize() {
    var latitude = 0;
    var longitude = 0;
    if (window.android){
      latitude = window.android.getLatitude();
      longitude = window.android.getLongitude();
    }
    var myLatlng = new google.maps.LatLng(latitude,longitude);
    var myOptions = {
      zoom: 8,
      center: myLatlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    }
    map = new google.maps.Map(document.getElementById("map_canvas"),
      myOptions);
  }
  function centerAt(latitude, longitude){
    myLatlng = new google.maps.LatLng(latitude,longitude);
    map.panTo(myLatlng);
  }
</script>