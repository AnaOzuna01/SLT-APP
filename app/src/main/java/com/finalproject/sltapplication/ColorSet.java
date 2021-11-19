package com.finalproject.sltapplication;

public class ColorSet {

    public String getRGBName(String colorName){

        String name;
        switch (colorName.toUpperCase()){
            case "#FF2B1AFF":
                name = "Azul";
                break;
            case "#FF5D9BFF":
                name = "Morado Claro";
                break;
            case "#FF90033FF":
                name = "Lila";
                break;
            case "#FFFAE1FF":
                name = "Blanco";
                break;
            case "#FFFF2985":
                name = "Amarillo";
                break;
            case "#FFFF2931":
                name = "Amarillo Brillante";
                break;
            case "#FF87FF62":
                name = "Gris";
                break;
            case "#FF6EFF3C":
                name = "Olivo";
                break;
            case "#FF1EFFC3":
                name = "Azul Marino";
                break;
            case "#FFE9967A":
                name = "Dark Salmon";
                break;
            case "#FFFF0000":
                name = "Red";
                break;
            case "#FF8B0000":
                name = "Dark Red";
                break;
            case "#FFFFA500":
                name = "Orange";
                break;
            case "#FFFF8C00":
                name = "Dark Orange";
                break;
            case "#FFFF7F50":
                name = "Coral";
                break;
            case "#FFFF6347":
                name = "Tomato";
                break;
            case "#FFFF4500":
                name = "Orange Red";
                break;
            case "#FFFFD700":
                name = "Gold";
                break;
            case "#FFFFFF01":
                name = "Yellow";
                break;
            case "#FFADFF2F":
                name = "Green Yellow";
                break;
            case "#FF00FF00":
                name = "Lime";
                break;
            case "#FF90EE90":
                name = "Light Green";
                break;
            case "#FF008000":
                name = "Green";
                break;
            case "#FF006400":
                name = "Dark Green";
                break;
            case "#FF6B8E23":
                name = "Olive";
                break;
            case "#FF00FFFF":
                name = "Aqua";
                break;
            case "#FFE0FFFF":
                name = "Cyan";
                break;
            case "#FFADD8E6":
                name = "Light Blue";
                break;
            case "#FF87CEEB":
                name = "Sky Blue";
                break;
            case "#FF001AFF":
                name = "Blue";
                break;
            case "#FF0000CD":
                name = "Medium Blue";
                break;
            case "#FF00008B":
                name = "Dark Blue";
                break;
            case "#FF000080":
                name = "Navy";
                break;
            case "#FF800000":
                name = "Maroon";
                break;
            case "#FFA52A2A":
                name = "Brown";
                break;
            case "#FFFFFFFF":
                name = "White";
                break;
            case "#FFF0FFFF":
                name = "Azure";
                break;
            case "#FF808080":
                name = "Gray";
                break;
            case "#FF000000":
                name = "Black";
                break;
            case "#FFC0C0C0":
                name = "Silver";
                break;
            default:
                name = "Unknown";
                break;
        }
        return name;
    }
}
