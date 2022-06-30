
package com.example.wear.tiles

import androidx.core.content.ContextCompat
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders.degrees
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.DimensionBuilders.expand
import androidx.wear.tiles.LayoutElementBuilders.ARC_ANCHOR_START
import androidx.wear.tiles.LayoutElementBuilders.Arc
import androidx.wear.tiles.LayoutElementBuilders.ArcLine
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.FontStyles
import androidx.wear.tiles.LayoutElementBuilders.Image
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.Spacer
import androidx.wear.tiles.LayoutElementBuilders.Text
import androidx.wear.tiles.ModifiersBuilders.Background
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ModifiersBuilders.Corner
import androidx.wear.tiles.ModifiersBuilders.Modifiers
import androidx.wear.tiles.ModifiersBuilders.Padding
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import androidx.wear.tiles.TimelineBuilders.Timeline
import androidx.wear.tiles.TimelineBuilders.TimelineEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future

private const val RESOURCES_VERSION = "1"

// dimensions
private val PROGRESS_BAR_THICKNESS = dp(6f)
private val BUTTON_SIZE = dp(48f)
private val BUTTON_RADIUS = dp(24f)
private val BUTTON_PADDING = dp(12f)
private val VERTICAL_SPACING_HEIGHT = dp(8f)

private const val ARC_TOTAL_DEGREES = 360f

// identifiers
private const val ID_IMAGE_START_RUN = "image_start_run"
private const val ID_CLICK_START_RUN = "click_start_run"


class GoalsTileService : TileService() {
        private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: TileRequest) = serviceScope.future {

        val goalProgress = GoalsRepository.getGoalProgress()
    
        val deviceParams = requestParams.deviceParameters!!

        // Creates Tile.
        Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)

           
            .setTimeline(
                Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                Layout.Builder()
                                    .setRoot(
                                        layout(goalProgress, deviceParams)
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            ).build()
    }

    //  Supply resources (graphics) for the Tile.
    override fun onResourcesRequest(requestParams: ResourcesRequest) = serviceScope.future {
        Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .addIdToImageMapping(
                ID_IMAGE_START_RUN,
                ImageResource.Builder()
                    .setAndroidResourceByResId(
                        AndroidImageResourceByResId.Builder()
                            .setResourceId(R.drawable.ic_run)
                            .build()
                    )
                    .build()
            )
            .build()
    }

 
    override fun onDestroy() {
        super.onDestroy()
        // Cleans up the coroutine
        serviceScope.cancel()
    }

    // Create root Box layout and content.

    private fun layout(goalProgress: GoalProgress, deviceParameters: DeviceParameters) =
        Box.Builder()
            // Sets width and height to expand and take up entire Tile space.
            .setWidth(expand())
            .setHeight(expand())

            // Adds an [Arc] via local function.
            .addContent(progressArc(goalProgress.percentage))

    
            .addContent(
                Column.Builder()
                    // Adds a [Text] via local function.
                    .addContent(
                        currentStepsText(goalProgress.current.toString(), deviceParameters)
                    )
                    // Adds a [Text] via local function.
                    .addContent(
                        totalStepsText(
                            resources.getString(R.string.goal, goalProgress.goal),
                            deviceParameters
                        )
                    )
                  
                    .addContent(Spacer.Builder().setHeight(VERTICAL_SPACING_HEIGHT).build())
                    // Adds an [Image] via local function.
                    .addContent(startRunButton())
                    .build()
            )
            .build()
            
    private fun progressArc(percentage: Float) = Arc.Builder()
        .addContent(
            ArcLine.Builder()
                .setLength(degrees(percentage * ARC_TOTAL_DEGREES))
                .setColor(argb(ContextCompat.getColor(this, R.color.primary)))
                .setThickness(PROGRESS_BAR_THICKNESS)
                .build()
        )
        // Element will start at 12 o'clock or 0 degree position in the circle.
        .setAnchorAngle(degrees(0.0f))
        .setAnchorType(ARC_ANCHOR_START)
        .build()
    //  construct/stylize Text representations of the step count & goal.
  
    private fun currentStepsText(current: String, deviceParameters: DeviceParameters) = Text.Builder()
        .setText(current)
        .setFontStyle(FontStyles.display2(deviceParameters).build())
        .build()

    // Creates a [Text] with total step count goal and stylizes it.
    private fun totalStepsText(goal: String, deviceParameters: DeviceParameters) = Text.Builder()
        .setText(goal)
        .setFontStyle(FontStyles.title3(deviceParameters).build())
        .build()

    private fun startRunButton() =
        Image.Builder()
            .setWidth(BUTTON_SIZE)
            .setHeight(BUTTON_SIZE)
            .setResourceId(ID_IMAGE_START_RUN)
            .setModifiers(
                Modifiers.Builder()
                    .setPadding(
                        Padding.Builder()
                            .setStart(BUTTON_PADDING)
                            .setEnd(BUTTON_PADDING)
                            .setTop(BUTTON_PADDING)
                            .setBottom(BUTTON_PADDING)
                            .build()
                    )
                    .setBackground(
                        Background.Builder()
                            .setCorner(Corner.Builder().setRadius(BUTTON_RADIUS).build())
                            .setColor(argb(ContextCompat.getColor(this, R.color.primaryDark)))
                            .build()
                    )
                    .setClickable(
                        Clickable.Builder()
                            .setId(ID_CLICK_START_RUN)
                            .setOnClick(ActionBuilders.LoadAction.Builder().build())
                            .build()
                    )
                    .build()
            )
            .build()

}
