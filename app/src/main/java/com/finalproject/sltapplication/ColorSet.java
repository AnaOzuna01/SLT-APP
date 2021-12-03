package com.finalproject.sltapplication;

public class ColorSet {

    public String getRGBName(String colorName){

        String name;
        switch (colorName.toUpperCase()){
            case "#FF2B1AFF":
            case "ff304050":
            case "#FF00FFFF":
            case "#FFE0FFFF":
            case "#FFADD8E6":
            case "#FF87CEEB":
            case "#FF001AFF":
            case "#FF0000CD":
            case "#FF00008B":
            case "#FF000080":
            case "#FFF0FFFF":
            case "ff304060":
            case "ff2078c0":
            case "ff607090":
            case "ff283048":
            case "ffa8c0c8":
            case "ff5070a0":
            case "ff303850":
            case "ff202840":
            case "ff202038":
            case "ff282838":
            case "ff383848":
            case "ff485068":
            case "ff202838":
            case "ffb0b8c0":
            case "ff606888":
            case "ff383850":
            case "ff284068":
            case "ff687098":
            case "ff607098":
            case "ff7080a0":
            case "ff385878":
            case "ff404060":
            case "ffa8b0c0":
            case "ff384880":
            case "ff687090":
                name = "Azul";
                break;
            case "#FF5D9BFF":
                name = "Morado";
                break;
            case "#FF90033FF":
                name = "Lila";
                break;
            case "#FFFAE1FF":
            case "#FFFFFFFF":
            case "ffd0d0c8":
            case "ffd0d0d0":
            case "ff9098a8":
            case "ffa8a0a8":
                name = "Blanco";
                break;
            case "#FFFF2985":
            case "#FFFFFF01":
            case "#FFADFF2F":
            case "ffd8a808":
                name = "Amarillo";
                break;
            case "#FFFF2931":
                name = "Amarillo Brillante";
                break;
            case "#FF87FF62":
            case "#FF808080":
            case "ffb0b8c8":
            case "ffc0c0c8":
            case "ff8088a0":
            case "ff909090":
            case "ff787890":
            case "ffa8a8b0":
            case "ff484858":
            case "ff989090":
            case "ffb8b8c8":
            case "ff384050":
            case "ff506070":
            case "ffa8a8b8":
            case "ffb8b0c0":
            case "ff303848":
            case "ff90a080":
                name = "Gris";
                break;
            case "#FF1EFFC3":
                name = "Azul Marino";
                break;
            case "#FFE9967A":
                name = "Salmon";
                break;
            case "#FFFF0000":
            case "#FF8B0000":
            case "#FFFF6347":
            case "#FFFF4500":
                name = "Rojo";
                break;
            case "#FFFFA500":
            case "#FFFF8C00":
                name = "Naranja";
                break;
            case "#FFFF7F50":
                name = "Coral";
                break;
            case "#FFFFD700":
                name = "Oro";
                break;
            case "#FF00FF00":
            case "#FF6EFF3C":
            case "#FF90EE90":
            case "#FF008000":
            case "#FF006400":
            case "#FF6B8E23":
                name = "Verde";
                break;
            case "#FF800000":
            case "#FFA52A2A":
                name = "Marron";
                break;
            case "#FF000000":
            case "ff303040":
            case "ff404050":
            case "ff505060":
            case "ff283038":
                name = "Negro";
                break;
            case "#FFC0C0C0":
            case "ffb0b0b0":
            case "ffc8c0c0":
            case "ffc0c0c0":
            case "ffc8c0c8":
            case "ffb8b8b8":
                name = "Plateado";
                break;
            case "ffa85078":
            case "ff906098":
                name = "Rosa";
                break;
            case "ff283040":
            case "ff303048":
            case "ff404048":
                name = "Negro";
                break;
            case "ff807078":
                name = "gris";
                break;
            case "ff889870":
                name = "Verde";
                break;
            case "ffa04860":
                name = "Rojo";
                break;
            case "ffa0a0a0":
                name = "Plateado";
                break;
            case "ff383838":
                name = "Negro";
                break;
            case "ff884058":
                name = "Rojizo";
                break;
            case "ff98b0d0":
                name = "Celeste";
                break;
            case "ffc88088":
                name = "Rosado";
                break;
            case "ff902830":
                name = "Rojo";
                break;
            case "ff381818":
                name = "Marron";
                break;
            case "ff282830":
                name = "Negro";
                break;
            case "ff9898a8":
                name = "Gris";
                break;
            case "ff181828":
                name = "Negro";
                break;
            case "ffb0b8b8":
                name = "Gris";
                break;
            case "ff606880":
                name = "Gris";
                break;
            case "ff181820":
                name = "Negro";
                break;
            case "ff202030":
                name = "Negro";
                break;
            case "ff302838":
                name = "Negro";
                break;
            case "ffa02828":
                name = "Rojo";
                break;
            case "ff182838":
                name = "Negro";
                break;
            case "ffb03858":
                name = "Rojo";
                break;
            case "ff888888":
                name = "";
                break;
            case "ff301818":
                name = "Rojo";
                break;
            default:
                name = "Desconocido";
                break;
        }
        return name;
    }
}
