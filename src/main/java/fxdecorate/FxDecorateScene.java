/*
 * This file is part of FxDecorate.
 *
 * Copyright (c) 2013 narrowtux <http://www.narrowtux.com/>
 * FxDecorate is licensed under the GNU Lesser General Public License.
 *
 * FxDecorate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FxDecorate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fxdecorate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
//https://github.com/rahmanusta/FxDecorate
public class FxDecorateScene extends Scene {

	private Node content;
	private SimpleIntegerProperty edgeSize = new SimpleIntegerProperty(1);
	private Rectangle resizeWest, resizeEast, resizeNorth, resizeSouth;
	private Rectangle resizeNorthEast, resizeNorthWest, resizeSouthEast, resizeSouthWest;
	private AnchorPane pane;
	private Stage stage;
	private FxDecorateController controller;

	public FxDecorateScene(Node content, Stage stage) {
		super(new AnchorPane());
		controller = new FxDecorateController(this);
		pane = (AnchorPane) getRoot();
		this.stage = stage;

		Color edges = Color.TRANSPARENT;

		// Align content
		this.content = content;
		pane.getChildren().add(content);
		AnchorPane.setLeftAnchor(content, 0d);
		AnchorPane.setTopAnchor(content, 0d);
		AnchorPane.setRightAnchor(content, 0d);
		AnchorPane.setBottomAnchor(content, 0d);

		// Align west resize handle
		resizeWest = new Rectangle();
		resizeWest.setCursor(Cursor.W_RESIZE);
		resizeWest.setFill(edges);
		pane.getChildren().add(resizeWest);
		resizeWest.widthProperty().bind(edgeSize);
		resizeWest.heightProperty().bind(heightProperty());
		AnchorPane.setLeftAnchor(resizeWest, 0d);
		AnchorPane.setTopAnchor(resizeWest, 0d);
		controller.setResizeableHandle(resizeWest);

		// Align top border
		resizeNorth = new Rectangle();
		resizeNorth.setCursor(Cursor.N_RESIZE);
		resizeNorth.setFill(edges);
		pane.getChildren().add(resizeNorth);
		resizeNorth.widthProperty().bind(widthProperty());
		resizeNorth.heightProperty().bind(edgeSize);
		AnchorPane.setLeftAnchor(resizeNorth, 0d);
		AnchorPane.setTopAnchor(resizeNorth, 0d);
		controller.setResizeableHandle(resizeNorth);

		// Align right border
		resizeEast = new Rectangle();
		resizeEast.setCursor(Cursor.E_RESIZE);
		resizeEast.setFill(edges);
		pane.getChildren().add(resizeEast);
		resizeEast.widthProperty().bind(edgeSize);
		resizeEast.heightProperty().bind(heightProperty());
		AnchorPane.setTopAnchor(resizeEast, 0d);
		AnchorPane.setRightAnchor(resizeEast, 0d);
		controller.setResizeableHandle(resizeEast);

		// Align bottom border
		resizeSouth = new Rectangle();
		resizeSouth.setCursor(Cursor.S_RESIZE);
		resizeSouth.setFill(edges);
		pane.getChildren().add(resizeSouth);
		resizeSouth.widthProperty().bind(widthProperty());
		resizeSouth.heightProperty().bind(edgeSize);
		AnchorPane.setLeftAnchor(resizeSouth, 0d);
		AnchorPane.setBottomAnchor(resizeSouth, 0d);
		controller.setResizeableHandle(resizeSouth);

		Color corners = Color.TRANSPARENT;

		// Align north-west resize handle
		resizeNorthWest = new Rectangle();
		resizeNorthWest.setCursor(Cursor.NW_RESIZE);
		resizeNorthWest.setFill(corners);
		pane.getChildren().add(resizeNorthWest);
		resizeNorthWest.widthProperty().bind(edgeSize);
		resizeNorthWest.heightProperty().bind(edgeSize);
		AnchorPane.setLeftAnchor(resizeNorthWest, 0d);
		AnchorPane.setTopAnchor(resizeNorthWest, 0d);
		controller.setResizeableHandle(resizeNorthWest);

		// Align north-east resize handle
		resizeNorthEast = new Rectangle();
		resizeNorthEast.setCursor(Cursor.NE_RESIZE);
		resizeNorthEast.setFill(corners);
		pane.getChildren().add(resizeNorthEast);
		resizeNorthEast.widthProperty().bind(edgeSize);
		resizeNorthEast.heightProperty().bind(edgeSize);
		AnchorPane.setRightAnchor(resizeNorthEast, 0d);
		AnchorPane.setTopAnchor(resizeNorthEast, 0d);
		controller.setResizeableHandle(resizeNorthEast);

		// Align south-east resize handle
		resizeSouthEast = new Rectangle();
		resizeSouthEast.setCursor(Cursor.SE_RESIZE);
		resizeSouthEast.setFill(corners);
		pane.getChildren().add(resizeSouthEast);
		resizeSouthEast.widthProperty().bind(edgeSize);
		resizeSouthEast.heightProperty().bind(edgeSize);
		AnchorPane.setRightAnchor(resizeSouthEast, 0d);
		AnchorPane.setBottomAnchor(resizeSouthEast, 0d);
		controller.setResizeableHandle(resizeSouthEast);

		// Align south-west resize handle
		resizeSouthWest = new Rectangle();
		resizeSouthWest.setCursor(Cursor.SW_RESIZE);
		pane.getChildren().add(resizeSouthWest);
		resizeSouthWest.setFill(corners);
		resizeSouthWest.widthProperty().bind(edgeSize);
		resizeSouthWest.heightProperty().bind(edgeSize);
		AnchorPane.setLeftAnchor(resizeSouthWest, 0d);
		AnchorPane.setBottomAnchor(resizeSouthWest, 0d);
		controller.setResizeableHandle(resizeSouthWest);
	}

	public int getEdgeSize() {
		return edgeSize.get();
	}

	public void setEdgeSize(int size) {
		edgeSize.set(size);
	}

	public IntegerProperty edgeSizeProperty() {
		return edgeSize;
	}

	public Node getRootNode() {
		return content;
	}

	public FxDecorateController getController() {
		return controller;
	}

	public Stage getStage() {
		return stage;
	}
}
