package createLabel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CreateLabel {
  private static final String ROOT = "D:\\CSUEB\\lynne\\DataSet_NPY";
  private static final File FALLING = new File(Paths.get(ROOT, "Falling").toUri());
  private static final File WALKING = new File(Paths.get(ROOT, "Walking").toUri());
  private static final Random RANDOM = new Random();

  private static final Set<Integer> FALLING_SET = new HashSet<>();
  private static final Set<Integer> WALKING_SET = new HashSet<>();
  public List<String> generateLabel(double rate) {
    List<String> labels = generateLabel(FALLING, FALLING_SET, rate);
    labels.addAll(generateLabel(WALKING, WALKING_SET, rate));
    return labels;
  }

  private List<String> generateLabel(File category, Set<Integer> categorySet, double rate) {
    String[] fileNames = category.list();
    int expectedSize = (int)(fileNames.length * rate);
    List<String> result = new ArrayList<>(expectedSize);
    while (result.size() != expectedSize) {
      int random = RANDOM.nextInt(fileNames.length);
      if (categorySet.contains(random)) {
        continue;
      }
      categorySet.add(random);
      result.add(Paths.get(category.getAbsolutePath(), fileNames[random]).toString());
    }
    return result;
  }

  private void addRemains(List<String> list) {
    String[] fileNames = WALKING.list();
    for (int i = 0; i < fileNames.length; i++) {
      if (!WALKING_SET.contains(i)) {
        list.add(Paths.get(WALKING.getAbsolutePath(), fileNames[i]).toString());
      }
    }
    fileNames = FALLING.list();
    for (int i = 0; i < fileNames.length; i++) {
      if (!FALLING_SET.contains(i)) {
        list.add(Paths.get(FALLING.getAbsolutePath(), fileNames[i]).toString());
      }
    }
  }

  public void writeAsFile(List<String> list, String fileName) {
    try(FileWriter fileWriter = new FileWriter(Paths.get(ROOT, fileName).toFile())) {
      for (String filePath : list) {
        fileWriter.append(filePath).append("\n");
      }
    } catch (IOException e) {
      System.out.println("Something wrong: " + e.getStackTrace());
    }
  }

  public static void main(String[] args) {
    CreateLabel createLabel = new CreateLabel();
    List<String> trainings = createLabel.generateLabel(0.5);
    List<String> testings = createLabel.generateLabel(0.4);
    List<String> validations = createLabel.generateLabel(0.1);
    createLabel.addRemains(trainings);

    int expectedTotal = WALKING.list().length + FALLING.list().length;
    int actualTotal = trainings.size() + testings.size() + validations.size();
    System.out.println(String.format("Expected total files: %d. Actual total files: %d. trainings: %d, testing: %d, validations: %d", expectedTotal, actualTotal, trainings.size(), testings.size(), validations.size()));

    createLabel.writeAsFile(trainings, "training.txt");
    createLabel.writeAsFile(testings, "testing.txt");
    createLabel.writeAsFile(validations, "validation.txt");
  }
}
