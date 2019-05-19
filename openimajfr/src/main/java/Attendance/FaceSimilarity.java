package Attendance;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.FacePatchFeature.Extractor;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 * Example showing how to use the {@link FaceSimilarityEngine} class to compare
 * faces detected in two images.
 * 
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 * 
 */
public class FaceSimilarity {
	/**
	 * Main method for the example.
	 * 
	 * @param args
	 *            Ignored.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// first, we load two images
		final URL image1url = new URL(
				"http://s3.amazonaws.com/rapgenius/fema_-_39841_-_official_portrait_of_president-elect_barack_obama_on_jan-_13.jpg");
		final URL image2url = new URL(
				"http://nimg.sulekha.com/others/thumbnailfull/barack-obama-michelle-obama-mary-mcaleese-martin-mcaleese-2011-5-23-6-50-0.jpg");

		final FImage image1 = ImageUtilities.readF(new File("./ProfilePics/frcam-Shantanu Roy-1558302135198.jpg"));
		final FImage image2 = ImageUtilities.readF(new File("./ProfilePics/frcam-Shantanu Roy-1558302135198.jpg"));

		// then we set up a face detector; will use a haar cascade detector to
		// find faces, followed by a keypoint-enhanced detector to find facial
		// keypoints for our feature. There are many different combinations of
		// features and detectors to choose from.
		final HaarCascadeDetector detector = HaarCascadeDetector.BuiltInCascade.frontalface_alt2.load();
		final FKEFaceDetector kedetector = new FKEFaceDetector(detector);

		// now we construct a feature extractor - this one will extract pixel
		// patches around prominant facial keypoints (like the corners of the
		// mouth, etc) and build them into a vector.
		final Extractor extractor = new FacePatchFeature.Extractor();

		// in order to compare the features we need a comparator. In this case,
		// we'll use the Euclidean distance between the vectors:
		final FaceFVComparator<FacePatchFeature, FloatFV> comparator =
				new FaceFVComparator<FacePatchFeature, FloatFV>(FloatFVComparison.EUCLIDEAN);

		// Now we can construct the FaceSimilarityEngine. It is capable of
		// running the face detector on a pair of images, extracting the
		// features and then comparing every pair of detected faces in the two
		// images:
		final FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage> engine =
				new FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage>(kedetector, extractor, comparator);

		// we need to tell the engine to use our images:
		engine.setQuery(image1, "image1");
		engine.setTest(image2, "image2");

		// and then to do its work of detecting, extracting and comparing
		engine.performTest();

		// finally, for this example, we're going to display the "best" matching
		// faces in the two images. The following loop goes through the map of
		// each face in the first image to all the faces in the second:
		for (final Entry<String, Map<String, Double>> e : engine.getSimilarityDictionary().entrySet()) {
			// this computes the matching face in the second image with the
			// smallest distance:
			double bestScore = Double.MAX_VALUE;
			String best = null;
			for (final Entry<String, Double> matches : e.getValue().entrySet()) {
				if (matches.getValue() < bestScore) {
					bestScore = matches.getValue();
					best = matches.getKey();
				}
			}

			// and this composites the original two images together, and draws
			// the matching pair of faces:
			final FImage img = new FImage(image1.width + image2.width, Math.max(image1.height, image2.height));
			img.drawImage(image1, 0, 0);
			img.drawImage(image2, image1.width, 0);

			img.drawShape(engine.getBoundingBoxes().get(e.getKey()), 1F);

			final Rectangle r = engine.getBoundingBoxes().get(best);
			r.translate(image1.width, 0);
			img.drawShape(r, 1F);

			// and finally displays the result
			DisplayUtilities.display(img);
		}
	}
}