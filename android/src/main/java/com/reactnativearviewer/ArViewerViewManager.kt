package com.reactnativearviewer

import android.util.Log
import androidx.annotation.Nullable
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.google.ar.core.Config
import io.github.sceneview.ar.arcore.LightEstimationMode
import io.github.sceneview.ar.node.EditableTransform


class ArViewerViewManager : SimpleViewManager<ArViewerView>() {
  /**
   * Assign an identifier to each command supported
   */
  companion object {
    const val COMMAND_SNAPSHOT = 1
    const val COMMAND_RESET = 2
  }

  /**
   * Name the view
   */
  override fun getName() = "ArViewerView"

  /**
   * Create the view
   */
  override fun createViewInstance(reactContext: ThemedReactContext): ArViewerView {
    return ArViewerView(reactContext);
  }

  /**
   * Pause the AR session when the view gets removed
   */
  override fun onDropViewInstance(view: ArViewerView) {
    Log.d("ARview onDropViewInstance", "Stopping session");
    super.onDropViewInstance(view)
    view.arSession?.pause()
    view.arSession?.close()
  }

  /**
   * Map the commands to an integer
   */
  override fun getCommandsMap(): Map<String, Int>? {
    return MapBuilder.of(
      "takeScreenshot", COMMAND_SNAPSHOT,
      "reset", COMMAND_RESET
    )
  }

  /**
   * Map methods calls to view methods
   */
  override fun receiveCommand(view: ArViewerView, commandId: String?, @Nullable args: ReadableArray?) {
    super.receiveCommand(view, commandId, args)
    Log.d("ARview receiveCommand", commandId.toString());
    when (commandId!!.toInt()) {
      COMMAND_SNAPSHOT -> {
        if (args != null) {
          val requestId = args.getInt(0);
          view.takeScreenshot(requestId)
        }
      }
      COMMAND_RESET -> {
        view.resetModel()
      }
    }
  }

  /**
   * Register the view events
   */
  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
    return MapBuilder.of(
      "onDataReturned", MapBuilder.of("registrationName","onDataReturned"),
      "onError", MapBuilder.of("registrationName","onError")
    )
  }

  /**
   * Required prop: the model src (URI)
   */
  @ReactProp(name = "model")
  fun setModel(view: ArViewerView, model: String) {
    Log.d("ARview model", model);
    view.loadModel(model);
  }

  /**
   * Optional: the plane orientation detection (can be: horizontal, vertical, both, none)
   */
  @ReactProp(name = "planeOrientation")
  fun setPlaneOrientation(view: ArViewerView, planeOrientation: String) {
    Log.d("ARview planeOrientation", planeOrientation);
    when(planeOrientation) {
      "horizontal" -> view.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
      "vertical" -> view.planeFindingMode = Config.PlaneFindingMode.VERTICAL
      "both" -> view.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
      "none" -> view.planeFindingMode = Config.PlaneFindingMode.DISABLED
    }
  }

  /**
   * Optional: enable ARCode light estimation
   */
  @ReactProp(name = "lightEstimation")
  fun setPlaneOrientation(view: ArViewerView, lightEstimation: Boolean) {
    Log.d("ARview lightEstimation", lightEstimation.toString());
    if(lightEstimation) {
      view.lightEstimationMode = LightEstimationMode.AMBIENT_INTENSITY;
    } else {
      view.lightEstimationMode = LightEstimationMode.DISABLED
    }
  }


  /**
   * Optional: enable SceneView depth management
   */
  @ReactProp(name = "manageDepth")
  fun setManageDepth(view: ArViewerView, manageDepth: Boolean) {
    Log.d("ARview manageDepth", manageDepth.toString());
    view.depthEnabled = manageDepth
  }


  /**
   * Optional: allow user to pinch the model to zoom it
   */
  @ReactProp(name = "allowScale")
  fun setAllowScale(view: ArViewerView, allowScale: Boolean) {
    Log.d("ARview allowScale", allowScale.toString());
    if(allowScale) {
      view.addAllowTransform(EditableTransform.SCALE)
    } else {
      view.removeAllowTransform(EditableTransform.SCALE)
    }
  }

  /**
   * Optional: allow user to translate the model
   */
  @ReactProp(name = "allowTranslate")
  fun setAllowTranslate(view: ArViewerView, allowTranslate: Boolean) {
    Log.d("ARview allowTranslate", allowTranslate.toString());
    if(allowTranslate) {
      view.addAllowTransform(EditableTransform.POSITION)
    } else {
      view.removeAllowTransform(EditableTransform.POSITION)
    }
  }

  /**
   * Optional: allow the user to rotate the model
   */
  @ReactProp(name = "allowRotate")
  fun setAllowRotate(view: ArViewerView, allowRotate: Boolean) {
    Log.d("ARview allowRotate", allowRotate.toString());
    if(allowRotate) {
      view.addAllowTransform(EditableTransform.ROTATION)
    } else {
      view.removeAllowTransform(EditableTransform.ROTATION)
    }
  }

  /**
   * Optional: disable the text instructions
   */
  @ReactProp(name = "disableInstructions")
  fun disableInstructions(view: ArViewerView, isDisabled: Boolean) {
    Log.d("ARview setInstructions", isDisabled.toString());
    view.setInstructionsEnabled(!isDisabled)
  }

  /**
   * Optional: disable the text instructions
   */
  @ReactProp(name = "disableInstantPlacement")
  fun disableInstantPlacement(view: ArViewerView, isDisabled: Boolean) {
    Log.d("ARview disableInstantPlacement", isDisabled.toString());
    view.instantPlacementEnabled = !isDisabled
  }
}
