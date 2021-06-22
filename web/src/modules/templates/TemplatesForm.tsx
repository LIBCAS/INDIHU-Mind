import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Add from "@material-ui/icons/Add";
import classNames from "classnames";
import { Field, FieldProps, Form, FormikProps } from "formik";
import React, {
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";
import { ButtonGrey } from "../../components/control/ButtonGrey";
import { Divider } from "../../components/divider/Divider";
import { Formik } from "../../components/form/Formik";
import { InputText } from "../../components/form/InputText";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { Popover } from "../../components/portal/Popover";
import { templateGet } from "../../context/actions/template";
import { GlobalContext } from "../../context/Context";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { theme } from "../../theme/theme";
import {
  CardTemplateAttribute,
  CardTemplateProps,
} from "../../types/cardTemplate";
import { notEmpty, notLongerThan255 } from "../../utils/form/validate";
import { TemplatesAddAttribute } from "./TemplatesAddAttribute";
import { TemplatesAttribute } from "./TemplatesAttribute";
import { useStyles } from "./_templatesStyles";
import { onSubmitTemplate } from "./_utils";

export interface TemplatesFormValues {
  id: string;
  name: string;
  attributeTemplates: CardTemplateAttribute[];
}

const initialValues = {
  id: "",
  name: "",
  attributeTemplates: [],
};

interface TemplatesFormProps {
  setShowModal: Function;
  item?: CardTemplateProps;
  refresh?: Function;
}

export const TemplatesForm: React.FC<TemplatesFormProps> = ({
  setShowModal,
  item,
  refresh,
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
  const [initValues, setInitValues] = useState<TemplatesFormValues>(
    initialValues
  );
  const AddAttributeRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);
  const onAddAttribute = useCallback(() => setPopoverOpen(true), []);

  useEffect(() => {
    if (item) {
      const { id, name, attributeTemplates } = item;
      setInitValues({
        id,
        name,
        attributeTemplates,
      });
    }
  }, [item]);

  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} />}
      <Formik
        initialValues={initValues}
        enableReinitialize
        onSubmit={(values: TemplatesFormValues) => {
          if (loading) return;
          setLoading(true);
          onSubmitTemplate(
            values,
            setShowModal,
            setError,
            setLoading,
            templateGet,
            dispatch,
            item
          );
          refresh && refresh();
        }}
        render={(formikBag: FormikProps<TemplatesFormValues>) => {
          return (
            <Form
              className={classNames(
                classesLayout.flex,
                classesLayout.directionColumn,
                classesLayout.spaceBetween,
                classes.templateFormWrapper
              )}
            >
              <div
                className={classNames(
                  classesLayout.flex,
                  classesLayout.flexWrap,
                  classesLayout.justifyCenter,
                  classesLayout.directionColumn,
                  classesSpacing.ml3,
                  classesSpacing.mr3
                )}
              >
                <Field
                  name="name"
                  validate={(value: any) =>
                    notLongerThan255(value) || notEmpty(value)
                  }
                  render={({
                    field,
                    form,
                  }: FieldProps<TemplatesFormValues>) => (
                    <InputText
                      label="Název"
                      type="text"
                      field={field}
                      form={form}
                      autoFocus={false}
                    />
                  )}
                />
                <Typography
                  className={classNames(
                    classesText.subtitle,
                    classesSpacing.mt2
                  )}
                  style={{ marginTop: theme.spacing(2) }}
                >
                  ATRIBUTY
                </Typography>
                <div
                  className={classNames(
                    classesLayout.flex,
                    classesLayout.flexWrap
                  )}
                >
                  {formikBag.values.attributeTemplates.map(
                    (att: CardTemplateAttribute, index: number) => {
                      return (
                        <TemplatesAttribute
                          key={att.id}
                          attribute={att}
                          formikBag={formikBag}
                          index={index}
                        />
                      );
                    }
                  )}
                </div>
                <div ref={AddAttributeRef} className={classes.addWrapper}>
                  <ButtonGrey
                    text="Přidat atribut"
                    onClick={onAddAttribute}
                    bold
                    Icon={<Add fontSize="small" />}
                  />
                  <Popover
                    open={popoverOpen}
                    setOpen={setPopoverOpen}
                    anchorEl={AddAttributeRef.current}
                    overflowVisible={true}
                    content={
                      <TemplatesAddAttribute
                        formikBagParent={formikBag}
                        setOpen={setPopoverOpen}
                      />
                    }
                  />
                </div>
              </div>
              <div>
                <Divider className={classesSpacing.mt3} />
                <div
                  className={classNames(
                    classesLayout.flex,
                    classesLayout.justifyCenter,
                    classesSpacing.mb1
                  )}
                >
                  <Button
                    className={classNames(
                      classesSpacing.mt3,
                      classesSpacing.mb2
                    )}
                    variant="contained"
                    color="primary"
                    type="submit"
                  >
                    {item ? "Změnit šablonu" : "Vytvořit šablonu"}
                  </Button>
                </div>
              </div>
            </Form>
          );
        }}
      />
    </>
  );
};
