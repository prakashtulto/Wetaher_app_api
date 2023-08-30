package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {

        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("jaipur")

        Searchcity()


    }

    private fun Searchcity() {
        val searchview = binding.search4iew
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Here, you can handle text change in the search view if needed.
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "1a8c143a57ab5dfbd9b94e7b7a0fcc09", "metric")

        response.enqueue(object : Callback<WeatherApp> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val temperature = responseBody.main.temp.toString()
                        val humidity=responseBody.main.humidity
                        val cityname=responseBody.name.toString()
                        val winspeed =responseBody.wind.speed
                        val sunRise=responseBody.sys.sunrise.toLong()
                        val sunSet= responseBody.sys.sunset.toLong()
                        val seaLevel=responseBody.main.pressure
                        val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                        val maxTemp=responseBody.main.temp_max
                        val minTemp=responseBody.main.temp_min


                        binding.temperature.text ="$temperature °C"
                        binding.weather.text=condition
                        binding.maxTemp.text="Max temp:$maxTemp °C"
                        binding.minTemp.text="min temp:$minTemp °C"
                        binding.humidity.text="$humidity %"
                        binding.windspeed.text="$winspeed"
                        binding.sunrise.text="${time(sunRise)}"
                        binding.sunset.text="${time(sunSet)}"
                        binding.sea.text="$seaLevel hpa"
                        binding.cityname.text="$cityname"
                        binding.Day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityname.text="$cityname"


                        changesImageAccordinigtoWeather(condition)



                        Log.d("tag", "Temperature: $temperature")
                    } else {
                        Log.e("tag", "Response body is null.")
                    }
                } else {
                    Log.e("tag", "Response unsuccessful. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("tag", "API call failed: ${t.message}")
            }
        })


    }

    private fun changesImageAccordinigtoWeather(conditions:String) {

        when(conditions){

            "Clear Sky","Haze","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimation.setAnimation(R.raw.sun)
            }

            "Clear Sky ","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimation.setAnimation(R.raw.cloud)
            }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimation.setAnimation(R.raw.rain)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimation.setAnimation(R.raw.snow)
            }
            else ->{

                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimation.setAnimation(R.raw.sun)

            }


        }
        binding.lottieAnimation.playAnimation()


    }

    private fun date(): String {
        val sdf =SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format(Date())


    }


    private fun time(timeStamp:Long): String {
        val sdf =SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp*1000))


    }

    fun dayName(timeStamp:Long):String{

        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())

    }


}

