import React from "react";
import MUITimeline from "@material-ui/lab/Timeline";
import TimelineItem from "@material-ui/lab/TimelineItem";
import TimelineSeparator from "@material-ui/lab/TimelineSeparator";
import TimelineDot from "@material-ui/lab/TimelineDot";
import TimelineConnector from "@material-ui/lab/TimelineConnector";
import TimelineContent from "@material-ui/lab/TimelineContent";
import TimelineOppositeContent from "@material-ui/lab/TimelineOppositeContent";
import Typography from "@material-ui/core/Typography";

type Item = any;

interface TimelineProps {
  items: Item[];
  labelLeftMapper: (item: Item) => any;
  labelRightMapper: (item: Item, index: number) => any;
}

export const Timeline: React.FC<TimelineProps> = ({
  items,
  labelLeftMapper,
  labelRightMapper,
}) => {
  return (
    <MUITimeline>
      {items.map((item, i) => {
        const label = labelLeftMapper(item);
        return (
          <TimelineItem key={`${label}-${i}`}>
            <TimelineOppositeContent>
              <Typography color="textSecondary">{label}</Typography>
            </TimelineOppositeContent>
            <TimelineSeparator>
              <TimelineDot />
              <TimelineConnector />
            </TimelineSeparator>
            <TimelineContent>{labelRightMapper(item, i)}</TimelineContent>
          </TimelineItem>
        );
      })}
    </MUITimeline>
  );
};
