package com.mycompany.estegnografia;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

/*
Lucas Riul Martins - 834823
João Vitor de Paula Souza - 835104
Marco Antônio Porsch no Henck Almeida - 833666
Cesare Crosara Cardoso - 834252
*/

public class Estegnografia {
    public static String encodeMessage(String userName, String imagePath, String message) {
        try {
            // Abre a imagem
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            // Converte a mensagem para uma lista de caracteres
            char[] chars = message.toCharArray();

            // Codifica a mensagem na imagem
            int charIndex = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (charIndex < chars.length) {
                        // Obtém o pixel no ponto (x, y)
                        int pixel = image.getRGB(x, y);

                        // Obtém os componentes de cor do pixel
                        int red = (pixel >> 16) & 0xFF;
                        int green = (pixel >> 8) & 0xFF;
                        int blue = pixel & 0xFF;

                        // Codifica o caractere nos bits menos significativos dos componentes de cor
                        red = (red & 0xFE) | ((chars[charIndex] >> 7) & 1);
                        green = (green & 0xFE) | ((chars[charIndex] >> 6) & 1);
                        blue = (blue & 0xFE) | ((chars[charIndex] >> 5) & 1);

                        // Atualiza o pixel com os novos componentes de cor
                        pixel = (red << 16) | (green << 8) | blue;
                        image.setRGB(x, y, pixel);

                        // Incrementa o índice do caractere
                        charIndex++;
                    }
                }
            }
            
            // Salva a imagem codificada
            String encodedImagePath = "C:/Users/" + userName + "/Downloads/imagem_codificada.bmp";
            ImageIO.write(image, "bmp", new File(encodedImagePath));

            System.out.println("Mensagem codificada com sucesso.");

            // Exibe a imagem original e a imagem codificada
            Desktop.getDesktop().open(new File(imagePath));
            Desktop.getDesktop().open(new File(encodedImagePath));
            
            //Retorna o caminho para a imagem codificada
            return encodedImagePath;
        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem: " + e.getMessage());
            return null;
        }
    }
    
    public static String decodeMessage(String imagePath) {
        try {
            // Abre a imagem codificada
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            // Lista para armazenar os valores ASCII decodificados
            List<Byte> asciiValues = new ArrayList<>();

            // Decodifica a mensagem da imagem
            int byteIndex = 0;
            byte currentByte = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);

                    // Obtém o valor do componente de cor vermelha
                    int red = (rgb >> 16) & 0xFF;

                    // Obtém o bit menos significativo do componente de cor vermelha
                    int bit = red & 1;

                    // Insere o bit no byte atual
                    currentByte = (byte) ((currentByte << 1) | bit);

                    // Incrementa o índice do byte
                    byteIndex++;

                    // Se todos os bits do byte foram processados
                    if (byteIndex % 8 == 0) {
                        // Adiciona o byte decodificado à lista
                        asciiValues.add(currentByte);

                        // Reinicia o byte atual
                        currentByte = 0;

                        // Verifica se a mensagem foi completamente decodificada
                        if (currentByte == 0) {
                            break;
                        }
                    }
                }
            }

            // Converte os valores ASCII decodificados em uma string
            byte[] asciiBytes = new byte[asciiValues.size()];
            for (int i = 0; i < asciiValues.size(); i++) {
                asciiBytes[i] = asciiValues.get(i);
            }
            String decodedMessage = new String(asciiBytes);

            System.out.println("Mensagem decodificada com sucesso: " + decodedMessage);

            // Retorna a mensagem decodificada
            return decodedMessage;
        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem: " + e.getMessage());
            return null;
        }
    }


    public static void main(String[] args) throws IOException {
        String userName = System.getProperty("user.name");
        String imagePath = "C:/Users/" + userName + "/Downloads/imagem.jpg";
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Escreva uma mensagem para ser codificada na imagem: ");
        String message = myObj.nextLine();
        String encodedImagePath = encodeMessage(userName, imagePath, message);
        if(encodedImagePath == null){
            System.out.println("Ocorreu um erro!");
        }
        else{
            String msgDecodificada = decodeMessage(encodedImagePath);
            if (msgDecodificada != null) {
                System.out.println("Mensagem decodificada: " + msgDecodificada);
            } else {
                System.out.println("Nenhuma mensagem codificada encontrada na imagem.");
            }
        }
    }
}