package com.mycompany.estegnografia;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

/*
Lucas Riul Martins - 834823
João Vitor de Paula Souza - 835104
Marco Antônio Porsch no Henck Almeida - 833666
Cesare Crosara Cardoso - 834252

*/
public class Estegnografia {
    public static String encodeMessage(String imagePath, String message) {
        try {
            // Abre a imagem
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            // Converte a mensagem para uma lista de valores ASCII
            byte[] asciiValues = message.getBytes();

            // Verifica se a mensagem pode caber na imagem
            int maxBytes = width * height * 3 / 8;
            if (asciiValues.length > maxBytes) {
                System.out.println("A mensagem é muito longa para codificar nesta imagem.");
            }

            // Codifica a mensagem na imagem
            int byteIndex = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);

                    // Obtém o valor do componente de cor atual
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Codifica um byte da mensagem em cada componente R, G, B
                    for (int bitIndex = 7; bitIndex >= 0; bitIndex--) {
                        if (byteIndex >= asciiValues.length) {
                            break;
                        }

                        // Obtém o próximo bit do byte da mensagem
                        int bit = (asciiValues[byteIndex] >> bitIndex) & 1;

                        // Define o bit menos significativo para o próximo bit do byte da mensagem
                        red = (red & 0xFE) | bit;

                        // Atualiza o valor do componente de cor no pixel
                        rgb = (red << 16) | (green << 8) | blue;

                        // Atualiza o pixel na imagem
                        image.setRGB(x, y, rgb);

                        // Incrementa o índice do byte
                        byteIndex++;
                    }
                }
            }

            // Salva a imagem codificada
            String encodedImagePath = "C:/Users/marco/Downloads/imagem_codificada.bmp";
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
            // Abre a imagem
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            // Variável para armazenar a mensagem decodificada
            StringBuilder decodedMessage = new StringBuilder();

            // Decodifica a mensagem da imagem
            int byteIndex = 0;
            byte currentByte = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);

                    // Obtém o valor do componente de cor vermelho
                    int red = (rgb >> 16) & 0xFF;

                    // Obtém o último bit do componente de cor vermelho
                    int bit = red & 1;

                    // Adiciona o bit decodificado ao byte atual
                    currentByte = (byte) ((currentByte << 1) | bit);

                    // Incrementa o índice do byte
                    byteIndex++;

                    // Se todos os bits do byte foram lidos
                    if (byteIndex % 8 == 0) {
                        // Verifica se o byte lido é um caractere de terminação
                        if (currentByte == 0) {
                            return decodedMessage.toString(); // Retorna a mensagem decodificada
                        } else {
                            // Converte o byte para um caractere ASCII e adiciona à mensagem decodificada
                            decodedMessage.append((char) currentByte);
                            currentByte = 0; // Reinicia o byte atual para ler o próximo byte
                        }
                    }
                }
            }

            System.out.println("Não foi encontrada uma mensagem terminada corretamente.");

            return null;
        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        String imagePath = "C:/Users/marco/Downloads/imagem.jpg";
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Escreva uma mensagem para ser codificada na imagem");
        String message = myObj.nextLine();
        String encodedImagePath = encodeMessage(imagePath, message);
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