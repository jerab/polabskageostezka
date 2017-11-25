package cz.polabskageostezka.utils.ar_utils;

import java.nio.Buffer;

/**
 * Created by tomason on 10.06.2017.
 */

public class BaseModel extends MeshObject {
	@Override
	public Buffer getBuffer(BUFFER_TYPE bufferType) {
		return null;
	}

	@Override
	public int getNumObjectVertex() {
		return 0;
	}

	@Override
	public int getNumObjectIndex() {
		return 0;
	}
}
