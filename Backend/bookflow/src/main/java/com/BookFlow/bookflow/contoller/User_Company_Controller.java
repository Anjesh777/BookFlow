package com.BookFlow.bookflow.contoller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("all")
public class User_Company_Controller {

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("getListOFDistrict")
    public List<String> getListOFDistrict(){

    return List.of(

            "Taplejung, Phungling",
            "Taplejung, Suketar",
            "Panchthar, Phidim",
            "Panchthar, Yangnam",
            "Ilam, Ilam",
            "Ilam, Pashupatinagar",
            "Jhapa, Birtamod",
            "Jhapa, Damak",
            "Morang, Biratnagar",
            "Morang, Urlabari",
            "Sunsari, Dharan",
            "Sunsari, Itahari",
            "Dhankuta, Dhankuta",
            "Dhankuta, Pakhribas",
            "Terhathum, Myanglung",
            "Terhathum, Basantapur",
            "Sankhuwasabha, Khandbari",
            "Sankhuwasabha, Chainpur",
            "Bhojpur, Bhojpur",
            "Bhojpur, Dingla",
            "Solukhumbu, Salleri",
            "Solukhumbu, Namche Bazaar",
            "Okhaldhunga, Siddhicharan",
            "Okhaldhunga, Rumjatar",
            "Khotang, Diktel",
            "Khotang, Halesi",
            "Udayapur, Gaighat",
            "Udayapur, Katari",

            // Madhesh Province
            "Saptari, Rajbiraj",
            "Saptari, Bhardaha",
            "Siraha, Siraha",
            "Siraha, Lahan",
            "Dhanusha, Janakpur",
            "Dhanusha, Dhanushadham",
            "Mahottari, Jaleshwar",
            "Mahottari, Bardibas",
            "Sarlahi, Malangwa",
            "Sarlahi, Lalbandi",
            "Rautahat, Gaur",
            "Rautahat, Chandrapur",
            "Bara, Kalaiya",
            "Bara, Nijgadh",
            "Parsa, Birgunj",
            "Parsa, Pokhariya",

            // Bagmati Province
            "Dolakha, Charikot",
            "Dolakha, Jiri",
            "Sindhupalchok, Chautara",
            "Sindhupalchok, Melamchi",
            "Rasuwa, Dhunche",
            "Rasuwa, Syafrubesi",
            "Dhading, Nilkantha",
            "Dhading, Gajuri",
            "Nuwakot, Bidur",
            "Nuwakot, Trishuli",
            "Kathmandu, Kathmandu",
            "Kathmandu, Kirtipur",
            "Bhaktapur, Bhaktapur",
            "Bhaktapur, Thimi",
            "Lalitpur, Patan",
            "Lalitpur, Godawari",
            "Kavrepalanchok, Dhulikhel",
            "Kavrepalanchok, Banepa",
            "Ramechhap, Manthali",
            "Ramechhap, Ramechhap",
            "Sindhuli, Kamalamai",
            "Sindhuli, Dudhauli",
            "Makwanpur, Hetauda",
            "Makwanpur, Manahari",
            "Chitwan, Bharatpur",
            "Chitwan, Ratnanagar",

            // Gandaki Province
            "Gorkha, Gorkha",
            "Gorkha, Palungtar",
            "Manang, Chame",
            "Manang, Manang",
            "Mustang, Jomsom",
            "Mustang, Lo Manthang",
            "Myagdi, Beni",
            "Myagdi, Galeshwor",
            "Kaski, Pokhara",
            "Kaski, Lekhnath",
            "Lamjung, Besishahar",
            "Lamjung, Sundarbazar",
            "Tanahu, Damauli",
            "Tanahu, Bandipur",
            "Nawalpur, Kawasoti",
            "Nawalpur, Gaindakot",
            "Syangja, Putalibazar",
            "Syangja, Waling",
            "Parbat, Kusma",
            "Parbat, Phalebas",
            "Baglung, Baglung",
            "Baglung, Galkot",

            // Lumbini Province
            "Gulmi, Tamghas",
            "Gulmi, Resunga",
            "Palpa, Tansen",
            "Palpa, Rampur",
            "Rupandehi, Butwal",
            "Rupandehi, Bhairahawa",
            "Kapilvastu, Kapilvastu",
            "Kapilvastu, Krishnanagar",
            "Arghakhanchi, Sandhikharka",
            "Arghakhanchi, Sitganga",
            "Pyuthan, Pyuthan",
            "Pyuthan, Swargadwari",
            "Rolpa, Liwang",
            "Rolpa, Holeri",
            "Eastern Rukum, Rukumkot",
            "Eastern Rukum, Sisne",
            "Banke, Nepalgunj",
            "Banke, Kohalpur",
            "Bardiya, Gulariya",
            "Bardiya, Rajapur",
            "Dang, Ghorahi",
            "Dang, Tulsipur",

            // Karnali Province
            "Western Rukum, Musikot",
            "Western Rukum, Chaurjahari",
            "Salyan, Salyan",
            "Salyan, Bagchaur",
            "Dolpa, Dunai",
            "Dolpa, Juphal",
            "Jumla, Chandannath",
            "Jumla, Tatopani",
            "Mugu, Gamgadhi",
            "Mugu, Rara",
            "Humla, Simikot",
            "Humla, Hilsa",
            "Kalikot, Manma",
            "Kalikot, Khada",
            "Jajarkot, Khalanga",
            "Jajarkot, Chhedagad",
            "Dailekh, Narayan",
            "Dailekh, Dullu",
            "Surkhet, Birendranagar",
            "Surkhet, Gurbhakot",

            // Sudurpashchim Province
            "Bajura, Martadi",
            "Bajura, Kolti",
            "Bajhang, Chainpur",
            "Bajhang, Jayaprithvi",
            "Darchula, Darchula",
            "Darchula, Khalanga",
            "Baitadi, Dasharathchand",
            "Baitadi, Patan",
            "Dadeldhura, Amargadhi",
            "Dadeldhura, Parshuram",
            "Doti, Dipayal",
            "Doti, Silgadhi",
            "Achham, Mangalsen",
            "Achham, Sanphebagar",
            "Kailali, Dhangadhi",
            "Kailali, Tikapur",
            "Kanchanpur, Bhimdatta",
            "Kanchanpur, Belauri"


            );


    }


}
