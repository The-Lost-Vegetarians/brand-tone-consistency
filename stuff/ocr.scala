// package example

// import net.sourceforge.tess4j.Tesseract
// import net.sourceforge.tess4j.TesseractException
// import java.io.File

// object ocr {
//     def main(args: Array[String]): Unit = {
//         try {
//             val image = new File("D:/amrita/sem5/projects/scalaspark/images/lipton-1.png")

//             val tesseract = new Tesseract()
//             tesseract.setDatapath("tess")
//             // Optionally set language (default is "eng")
//             tesseract.setLanguage("eng")

//             val result = tesseract.doOCR(image)
//             println("Extracted Text:")
//             println(result)
//         } catch {
//             case e: TesseractException =>
//               println("Error while extracting text: " + e.getMessage)
//         }
//     }
// }
